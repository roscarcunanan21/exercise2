package exercise2;

import java.util.ArrayList;
import java.util.List;

public class Passenger {
	int id;
	int current_floor;
	int elevator_id;
	String direction;
	int destination_floor;
	String status;
	
	public void setElevatorId(int p_elevator_id){
		this.elevator_id = p_elevator_id;
	}
	
	public void setElevatorMaxDestinationFloor(){
		int elevator_id = this.elevator_id;
		for (Elevator elevator : ElevatorLogic.elevator_list) {
			// for each passenger not in transit, check if the assigned elevator is already in the same floor
			if (elevator.id == elevator_id){
				elevator.setMaxDestinationFloor();
			}
		}
	}
	
	public void setStatus(String p_status){
		this.status = p_status;
	}
	
	public void setCurrentFloor(int p_floor){
		this.current_floor = p_floor;
	}
	
	public void assignElevator(){
		// step 1: look for any passing elevators
		List<Elevator> passing_elevators = findPassingElevators(this.current_floor, this.direction);
		if (ElevatorLogic.debug_mode2){
			System.out.format(passing_elevators.size() + " passing elevators. | ");
		}		
		// step 2: look for stationary elevators
		List<Elevator> stationary_elevators = findStationaryElevators();
		if (ElevatorLogic.debug_mode2){
			System.out.format(stationary_elevators.size() + " stationary elevators.%n");
		}
		// step 3: combine the passing and stationary elevators
		List<Elevator> usable_elevators = new ArrayList<Elevator>(passing_elevators);
		usable_elevators.addAll(stationary_elevators);
		
		Elevator chosen_elevator = null;
		int chosen_elevator_id = -1;			
		// step 4: get the most viable elevator to use from the list
		if (usable_elevators.size() > 0){
			chosen_elevator = chooseElevator(usable_elevators, this.current_floor);
			// chosen_elevator.addPassenger();
			chosen_elevator_id = chosen_elevator.id;
		}
		if (ElevatorLogic.debug_mode){
			System.out.format("assigning elevator_id_%d%n",chosen_elevator_id);
		}
		
		this.elevator_id = chosen_elevator_id;
		this.setElevatorMaxDestinationFloor();
	}
	
	public static List<Elevator> findPassingElevators (int p_current_floor, String p_direction) {

		List<Elevator> elevators = new ArrayList<>();

		if (ElevatorLogic.debug_mode){
			System.out.format("-----------------%n");
			System.out.println("PASSING ELEVATOR SEARCH");			
		}
		for (final Elevator elevator : ElevatorLogic.elevator_list) {
			
			// if the elevator is not moving, skip it
			if (elevator.status == "stopped"){
				continue;
			}
			
			// if the elevators' max passenger count is met then skip it
			if (elevator.passenger_count >= ElevatorLogic.max_passengers_per_elevator){
				continue;
			}
			
			// passenger is going up
			if (p_direction == "up"){
				// passengers current floor must be higher or equal to elevators current floor and
				// passengers current floor must be lower than the max_destination_floor
				if ((p_current_floor >= elevator.current_floor) && (p_current_floor <= elevator.max_destination_floor)){	
					elevators.add(elevator);		
				}				
			}
			// passenger is going down
			else if (p_direction == "down"){
				// passengers current floor must be lower or equal to elevators current floor and
				// passengers current floor must be higher than the max_destination_floor
				if ((p_current_floor <= elevator.current_floor) && (p_current_floor >= elevator.max_destination_floor)){
					elevators.add(elevator);					
				}				
			}

			if (ElevatorLogic.debug_mode){
				System.out.println("-----------------");
				System.out.println("PASSING ELEVATOR FOUND:");
				System.out.println("elevator_id: " + elevator.id);
				System.out.println("passenger_count: " + elevator.passenger_count);
				System.out.println("current_floor: " + elevator.current_floor);
				System.out.println("status: " + elevator.status);	
			}	
		}
		
		return elevators;
	}
	
	public static List<Elevator> findStationaryElevators () {

		List<Elevator> elevators = new ArrayList<>();
		if (ElevatorLogic.debug_mode){
			System.out.format("-----------------%n");
			System.out.println("STATIONARY ELEVATOR SEARCH");			
		}
		for (final Elevator elevator : ElevatorLogic.elevator_list) {
			
			// the elevator must not be moving
			if (elevator.status != "stopped"){
				continue;
			}

			// if the elevators' max passenger count is met then skip it
			if (elevator.passenger_count >= ElevatorLogic.max_passengers_per_elevator){
				continue;
			}
			
			if (ElevatorLogic.debug_mode){
				System.out.println("-----------------");
				System.out.println("STATIONARY ELEVATOR FOUND:");
				System.out.println("elevator_id: " + elevator.id);
				System.out.println("passenger_count: " + elevator.passenger_count);
				System.out.println("current_floor: " + elevator.current_floor);
				System.out.println("status: " + elevator.status);				
			}
			elevators.add(elevator);				
		}
		
		return elevators;
	}
	
	public static Elevator chooseElevator ( List<Elevator> p_usable_elevators, int p_current_floor) {

		if (ElevatorLogic.debug_mode){
			System.out.format("-----------------%n");
			System.out.println("CHOOSING FINAL ELEVATOR");
		}
		
		Elevator closest_stationary_elevator = null;
		Elevator closest_passing_elevator = null;
		Elevator chosen_elevator = null;
		
		for (final Elevator elevator : p_usable_elevators) {
			
			// check if this is a stationary elevator
			if (elevator.status == "stopped"){
				
				// if we already stored the closest_stationary_elevator previously, compare it with the current one
				if (closest_stationary_elevator != null){
					if (Math.abs(elevator.current_floor - p_current_floor) < Math.abs(closest_stationary_elevator.current_floor - p_current_floor) ){
						closest_stationary_elevator = elevator;
					}					
				}
				// closest_stationary_elevator is not assigned yet, assign it here
				else{
					closest_stationary_elevator = elevator;
				}
			}
			// we will do the same for the passing elevator
			else
			{				
				// if we already stored the closest_stationary_elevator previously, compare it with the current one
				if (closest_passing_elevator != null){
					if (Math.abs(elevator.current_floor - p_current_floor) < Math.abs(closest_passing_elevator.current_floor - p_current_floor) ){
						closest_passing_elevator = elevator;
					}					
				}
				// closest_stationary_elevator is not assigned yet, assign it here
				else{
					closest_passing_elevator = elevator;
				}
			}
		}

		if (ElevatorLogic.debug_mode2){
			if (closest_stationary_elevator != null){				
				System.out.println("-----------------");
				System.out.println("CLOSEST STATIONARY ELEVATOR:");
				System.out.println("elevator_id: " + closest_stationary_elevator.id);
				System.out.println("passenger_count: " + closest_stationary_elevator.passenger_count);
				System.out.println("current_floor: " + closest_stationary_elevator.current_floor);
				System.out.println("status: " + closest_stationary_elevator.status);			
			}
			
			if (closest_passing_elevator != null){
				System.out.println("-----------------");
				System.out.println("CLOSEST PASSING ELEVATOR:");
				System.out.println("elevator_id: " + closest_passing_elevator.id);
				System.out.println("passenger_count: " + closest_passing_elevator.passenger_count);
				System.out.println("current_floor: " + closest_passing_elevator.current_floor);
				System.out.println("status: " + closest_passing_elevator.status);	
				System.out.println("-----------------");		
			}
		}

		if (ElevatorLogic.debug_mode2){
			System.out.format("----------------------------------------%n");
		}
		// now check which elevator is the closest
		// if we found the closest_passing_elevator but no closest_stationary_elevator
		if ((closest_stationary_elevator == null) && (closest_passing_elevator != null)){
			if (ElevatorLogic.debug_mode2){
				System.out.println("No stationary elevator found. choosing the passing elevator.");
			}
			chosen_elevator = closest_passing_elevator;
		}
		// if we found the closest_stationary_elevator but no closest_passing_elevator
		if ((closest_stationary_elevator != null) && (closest_passing_elevator == null)){
			if (ElevatorLogic.debug_mode2){
				System.out.println("No passing elevator found. choosing the stationary elevator.");
			}
			chosen_elevator = closest_stationary_elevator;
		}
		// if we have both then look for the nearest one
		if ((closest_stationary_elevator != null) && (closest_passing_elevator != null)){
			
			// STATIONARY ELEVATOR is closer
			if (Math.abs(closest_stationary_elevator.current_floor - p_current_floor) < Math.abs(closest_passing_elevator.current_floor - p_current_floor)){

				if (ElevatorLogic.debug_mode2){
					System.out.format("The stationary elevator is closer. The closest passing elevator is %d floors away.%n",Math.abs(closest_passing_elevator.current_floor - p_current_floor));
				}
				
				// if the passing elevator is more than N floors away then we will choose the stationary elevator
				if (Math.abs(closest_passing_elevator.current_floor - p_current_floor) >= ElevatorLogic.minimum_passing_elevator_floor_difference){
					if (ElevatorLogic.debug_mode2){
						System.out.format("The difference is greater or equal to the limit [%d]. choosing the stationary elevator instead.%n", ElevatorLogic.minimum_passing_elevator_floor_difference);					
					}
					chosen_elevator = closest_stationary_elevator;
				}else{
					if (ElevatorLogic.debug_mode2){
						System.out.format("The difference is lesser than the limit [%d]. choosing the passing elevator instead.%n", ElevatorLogic.minimum_passing_elevator_floor_difference);	
					}
					chosen_elevator = closest_passing_elevator;
				}
			}
			// PASSING ELEVATOR is closer
			else{
				if (ElevatorLogic.debug_mode2){
					System.out.format("assigning the passing elevator since it is closer%n");
				}
				chosen_elevator = closest_passing_elevator;
			}
		}
		if (ElevatorLogic.debug_mode2){
			System.out.format("----------------------------------------%n");
		}		
		return chosen_elevator;
	}
}
