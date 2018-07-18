package exercise2;

import java.util.Timer;
import java.util.TimerTask;

public class Simulator {
	
	private int seconds = 1;
	
	public void start(){	    
	    Timer t = new Timer();
	    t.scheduleAtFixedRate(
	    new TimerTask()
	    {
	        public void run()
	        {
	            // System.out.format("%d seconds passed%n", seconds);
    			
	    		// OPEN THE DOOR IF EACH ASSIGNED ELEVATOR REACHES EACH PASSENGER
	    		for (Passenger passenger : ElevatorLogic.passenger_list) {
					
	    			if (passenger.status == "waiting"){
						if (ElevatorLogic.debug_mode2){
							System.out.format("[passenger]: passenger_%d|passenger_status %s|current_floor %d|destination_floor %d|elevator_%d%n",passenger.id,passenger.status,passenger.current_floor,passenger.destination_floor,passenger.elevator_id);
						}
		    			passenger.assignElevator();	    				
	    			}
	    			
	    			for (Elevator elevator : ElevatorLogic.elevator_list) {
	    				// the current elevator has been assigned to this passenger
	    				if (passenger.elevator_id == elevator.id) {
		    				// for each passenger still waiting, check if the assigned elevator is already in the same floor
		    				if (passenger.status == "waiting"){
		    					// if the elevator is in the same floor as the passenger then open
		    					if(passenger.current_floor == elevator.current_floor){
			    					elevator.setStatus("door_opening");
			    					passenger.setStatus("in_transit");
			    					elevator.setMaxDestinationFloor();
			    					elevator.addPassenger();
			    					if (ElevatorLogic.debug_mode2){
			    						System.out.format("[passenger_entry]:   current_floor %d|destination_floor %d|elevator_%d|passenger_%d|%d total passengers.%n",passenger.current_floor,passenger.destination_floor,elevator.id,passenger.id,elevator.passenger_count);
			    					}
		    					}
		    					// otherwise set the elevator to go up or down
		    					else{
		    						if (passenger.current_floor < elevator.current_floor){
		    							elevator.setStatus("going_down");
		    						}
		    						else{
		    							elevator.setStatus("going_up");
		    						}
		    					}
		    				} 
		    				    				
		    				// if the passenger is in transit, check if the elevator is already in the destination floor
		    				else if (passenger.status == "in_transit"){
		    					// open the floor for the passenger assigned to this elevator
		    					if (passenger.destination_floor == elevator.current_floor){
			    					elevator.setStatus("door_opening");
			    					passenger.setStatus("arrived");
			    					elevator.removePassenger();
			    					if (ElevatorLogic.debug_mode2){
			    						System.out.format("[passenger_arrival]: current_floor %d|destination_floor %d|elevator_%d|passenger_%d|%d remaining passengers.%n",elevator.current_floor,passenger.destination_floor,elevator.id,passenger.id,elevator.passenger_count);
			    					}
			    					elevator.setMaxDestinationFloor();
			    				}
		    					// update the passengers current floor for clarity
		    					else{
		    						// passenger.setCurrentFloor(elevator.current_floor);
		    					}		    					
		    				}
	    				}
	    			}
	    		}

				if (ElevatorLogic.debug_mode2){
					System.out.println("--------------------------------");
				}
    			// LET THE ELEVATOR GO UP OR DOWN OR CLOSE THE DOOR IF IT IS OPENED
    			for (Elevator elevator : ElevatorLogic.elevator_list) {

					if (ElevatorLogic.debug_mode2){
						System.out.format("elevator_%s | current_floor_%s | status %s | max_destination_floor %d | passengers %d %n",elevator.id,elevator.current_floor,elevator.status,elevator.max_destination_floor,elevator.passenger_count);
					}
					
    				switch (elevator.status){
    					case "going_up":
    						// safety net to make sure floor doesnt exceed total
    						if (elevator.current_floor == ElevatorLogic.total_floors){
    							elevator.setStatus("stopped");
    							elevator.setMaxDestinationFloor();
    						}else{
        						elevator.goUp();    							
    						}
    						break;
    					case "going_down":
    						// safety net to ensure floor doesnt go lower than 1st
    						if (elevator.current_floor == 1){
    							elevator.setStatus("stopped");
    							elevator.setMaxDestinationFloor();
    						}else{
        						elevator.goDown();    							
    						}
    						break;
    					case "door_opening":
    						elevator.setStatus("door_closing");
    						break;
    					case "door_closing":
    						if (elevator.max_destination_floor > elevator.current_floor){
    							elevator.goUp();
    						}else{
    							elevator.goDown();
    						}
    						break;
    				}
    			}
	        }
	    }, 0, seconds * 1000);
	}
}
