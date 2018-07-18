package exercise2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElevatorLogic {
	
	// START CONFIG VARIABLES
	// if there is a stationary elevator closer to the passenger 
	// but the closest passing elevator is at most n floors away,
	// we will wait for the passing elevator instead
	public static int minimum_passing_elevator_floor_difference = 5;
	public static boolean debug_mode = false;
	public static boolean debug_mode2 = true;
	public static int elevator_count = 6;
	public static int total_floors = 20;
	public static int max_passengers_per_elevator = 6;
	public static int total_sample_passengers = 30;
	// END CONFIG VARIABLES
	
	// global variable for elevator list
	public static List<Elevator> elevator_list = new ArrayList<>();
	
	// global variable for elevator queue
	public static List<Passenger> passenger_list = new ArrayList<>();	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// generate random elevator objects
		// randomizeElevators(elevator_count, total_floors);
		initializeElevators(elevator_count);
		
		// we will create a batch of sample passengers
		initializeTrulyRandomBatchSamplePassengers();
		//initializeRandomBatchSamplePassengers();

		// we will start the simulation here
		Simulator runner = new Simulator();
		runner.start();
	}
	
	public static void initializeElevators (int p_elevator_count){

		if (debug_mode){
			System.out.format("%nInitializing elevator status:%n");	
		}	
		
		for (int elevator = 1 ; elevator <= p_elevator_count ; elevator++) {
			
			// initialize the elevator object
			Elevator current_elevator = new Elevator();

			current_elevator.id = elevator;
			current_elevator.current_floor = 1;
			current_elevator.status = "stopped";
			current_elevator.passenger_count = 0;

			if (debug_mode){
				System.out.println("-----------------");
				System.out.println("elevator_id: " + current_elevator.id);
				System.out.println("passenger_count: " + current_elevator.passenger_count);
				System.out.println("current_floor: " + current_elevator.current_floor);
				System.out.println("status: " + current_elevator.status);	
			}	
			elevator_list.add(current_elevator);
		}
	}
	
	public static void initializeRandomBatchSamplePassengers (){
		if (debug_mode){
			System.out.format("%nInitializing batch sample passengers:%n");
		}
		if (debug_mode2){
			System.out.format("%nInitializing batch sample passengers:%n");
		}

		int last_passenger_id = getLastPassengerId();
		
		for (int passenger = (last_passenger_id + 1) ; passenger <= total_sample_passengers ; passenger++) {
			
			// initialize the passenger object
			Passenger current_passenger = new Passenger();

			Random r = new Random();
			int Low = 2;
			int High = total_floors;
			
			current_passenger.id = passenger;
			current_passenger.current_floor = 1;
			current_passenger.direction = "up";
			current_passenger.status = "waiting";
			current_passenger.destination_floor = r.nextInt(High-Low) + Low;
			current_passenger.assignElevator();
			current_passenger.setElevatorMaxDestinationFloor();

			if (debug_mode){
				System.out.println("-----------------");
				System.out.println("passenger_id: " + current_passenger.id);
				System.out.println("current_floor: " + current_passenger.current_floor);
				System.out.println("status: " + current_passenger.status);
				System.out.println("destination_floor: " + current_passenger.destination_floor);
				System.out.println("passenger_elevator_id: " + current_passenger.elevator_id);
				System.out.println("direction: " + current_passenger.direction);
			}
			
			passenger_list.add(current_passenger);
		}
		if (debug_mode2){
			System.out.println("-----------------");
		}
	}
	
	public static void initializeTrulyRandomBatchSamplePassengers (){
		if (debug_mode){
			System.out.format("%nInitializing batch sample passengers:%n");
		}

		if (debug_mode2){
			System.out.format("%nInitializing batch sample passengers:%n");
		}
		
		int last_passenger_id = getLastPassengerId();
		
		for (int passenger = (last_passenger_id + 1) ; passenger <= total_sample_passengers ; passenger++) {
			
			// initialize the passenger object
			Passenger current_passenger = new Passenger();

			Random r = new Random();
			int Low;
			int High;

			// randomize if sample passenger is going up or down
			boolean going_up = (Math.random() < 0.5);
			if (going_up){		
				current_passenger.direction = "up";
				// if going up set the current floor to anywhere between first floor to the last floor - 1		
				Low = 1; High = total_floors - 1;
				current_passenger.current_floor = r.nextInt(High-Low) + Low;
				// now set the destination_floor to another random floor higher than the current floor
				Low = current_passenger.current_floor + 1; High = total_floors;
				current_passenger.destination_floor = r.nextInt(High-Low) + Low;
			}else{
				current_passenger.direction = "down";
				// if going down set the current floor to anywhere between second floor to the last floor			
				Low = 2; High = total_floors;
				current_passenger.current_floor = r.nextInt(High-Low) + Low;
				// now set the destination_floor to another random floor lower than the current floor
				if (current_passenger.current_floor == 2){
					current_passenger.destination_floor = 1;
				}else{
					Low = 1; High = current_passenger.current_floor - 1;
					current_passenger.destination_floor = r.nextInt(High-Low) + Low;				
				}
			}
			current_passenger.id = passenger;
			current_passenger.status = "waiting";
			// current_passenger.assignElevator();
			current_passenger.setElevatorMaxDestinationFloor();

			if (debug_mode2){
				System.out.println("-----------------");
				System.out.println("passenger_id: " + current_passenger.id);
				System.out.println("passenger_" + current_passenger.id);
				System.out.println("current_floor: " + current_passenger.current_floor);
				System.out.println("status: " + current_passenger.status);
				System.out.println("destination_floor: " + current_passenger.destination_floor);
				System.out.println("passenger_elevator_id: " + current_passenger.elevator_id);
				System.out.println("direction: " + current_passenger.direction);
			}
			
			passenger_list.add(current_passenger);
		}
		if (debug_mode2){
			System.out.println("-----------------");
		}
	}
	
	public static int getLastPassengerId(){

		if (debug_mode){
			System.out.format("%nGetting Last Passenger Id:%n");
		}

		int last_passenger_id = 0;
		for (Passenger passenger : passenger_list) {
			if (passenger.id > last_passenger_id) last_passenger_id = passenger.id;
		}
		return last_passenger_id;
	}
	
	public static void randomizeElevators (int p_elevator_count, int p_total_floors){

		if (debug_mode){
			System.out.format("%nRandomizing elevator status:%n");	
		}
		
		for (int elevator = 1 ; elevator <= p_elevator_count ; elevator++) {

			// initialize the randomization variables
			Random r = new Random();
			int Low = 1;
			int High = 1;
			
			System.out.println("-----------------");
			// initialize the elevator object
			Elevator current_elevator = new Elevator();

			current_elevator.id = elevator;
			
			// default the current capacity to full
			current_elevator.passenger_count = max_passengers_per_elevator;			
			// randomize true or false if elevator is full
			boolean is_full = (Math.random() < 0.5);
			// System.out.println("is_full: " + is_full);
			if (!is_full) {
				// if not full, default current_capacity to 0
				current_elevator.passenger_count = 0;
				// randomize true or false if elevator is empty
				boolean is_empty = (Math.random() < 0.5);
				// System.out.println("is_empty: " + is_empty);
				if (!is_empty) {
					Low = 1;
					High = max_passengers_per_elevator - 1;
					// if not empty then randomize capacity between 1 and 99
					current_elevator.passenger_count = r.nextInt(High-Low) + Low;
				}
			}
			
			// randomize between 1st floor and the total number of floors
			Low = 1;
			High = p_total_floors;
			current_elevator.current_floor = r.nextInt(High-Low) + Low;
			
			// default max_destination_floor to current_floor (static)
			current_elevator.status = "stopped";
			boolean going_up = (Math.random() < 0.5);
			// if the current capacity is not empty then it should be in motion
			if (current_elevator.passenger_count != 0) {
				if (going_up){
					current_elevator.status = "going_up";
				}else{
					current_elevator.status = "going_down";					
				}
			}

			if (debug_mode){
				System.out.println("passenger_elevator_id: " + current_elevator.id);
				System.out.println("passenger_count: " + current_elevator.passenger_count);
				System.out.println("current_floor: " + current_elevator.current_floor);
				System.out.println("status: " + current_elevator.status);
			}
			elevator_list.add(current_elevator);
		}
	}
}
