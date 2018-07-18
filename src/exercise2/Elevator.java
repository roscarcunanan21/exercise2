package exercise2;

public class Elevator {
	int id;
	int current_floor;
	String status; // going_up, going_down, stopped, door_opening, door_closing
	int max_destination_floor;
	int passenger_count;
	
	public void goUp(){
		this.current_floor = this.current_floor + 1;
		this.setStatus("going_up"); 
	}
	public void goDown(){
		this.current_floor = this.current_floor - 1;
		this.setStatus("going_down");
	}
	public void addPassenger(){
		this.passenger_count = this.passenger_count + 1;
	}
	public void removePassenger(){
		this.passenger_count = this.passenger_count - 1;
	}
	public void setStatus(String p_status){
		this.status = p_status;
	}
	public void setMaxDestinationFloor(){
		
		int max_destination_floor = 0;

		// go through each passenger in the queue
		for (Passenger passenger : ElevatorLogic.passenger_list) {
			// check if the passenger is assigned in this same elevator and if he hasnt arrived yet
			if ((passenger.elevator_id == this.id) && (passenger.status != "arrived")){
				
				// assign the first test value first. we cannot default to 0 all throughout because max_destination_floor
				// isnt higher or lower. it depends on the direction of the elevator
				if (max_destination_floor == 0){
					max_destination_floor = passenger.destination_floor;
				}
				// get the max destination floor out of all filtered passengers
				// passenger is going up
				if ((passenger.current_floor < passenger.destination_floor) && (passenger.destination_floor > max_destination_floor)){
					max_destination_floor = passenger.destination_floor;			
				}
				if ((passenger.current_floor > passenger.destination_floor) && (passenger.destination_floor < max_destination_floor)){
					max_destination_floor = passenger.destination_floor;			
				}
			}
		}

		if (ElevatorLogic.debug_mode){
			System.out.format("setting elevator [%d] max_destination_floor [%d]:%n", this.id, max_destination_floor);
		}
		
		// if the max_destination_floor hasnt been set because the unarrived passengers havent been assigned this elevator
		// then set the status to stopped so it wont go up or down anymore
		if (max_destination_floor == 0){
			this.status = "stopped";
			if (ElevatorLogic.debug_mode){
				System.out.format("                     setting elevator [%d] status to stopped.%n", this.id);
			}
		}
		this.max_destination_floor = max_destination_floor;
	}
}
