import java.util.*;
/**
 * Main class for the program where execution will be run
 * @author Christopher Leach
 * @version 0.1
 */
// under construction
public class Main {
    // TO ADD:
    // Create a list for stations
    private static ArrayList<Station> stations = new ArrayList<Station>();
    // Create a queue for trains
    private static QueueInterface<Train> trainQueue = new LinkedQueue<Train>();
    // Create a queue for passengers
    private static QueueInterface<Passenger> passengerQueue = new LinkedQueue<Passenger>();
    // variables for number of passengers
    private static int numPassengers = 0;
    // variables for passengers waiting
    private static int passengersWaiting = 0;
    // call method to create stations

    public static void main(String[] args) {
        //create client object
        Client sim = new Client();
        //call method to create stations
        sim.createStations(stations);
        //loop for time interval
        for (int i = 0; i < Client.TIME_INTERVAL; i++) {
            //report passengers waiting
            System.out.println("Passengers waiting: " + passengersWaiting);
            System.out.println("Time: " + i);
            System.out.println("Passengers on trains: " + Client.passengerOnTrains);
            //call method to start new train
            sim.startNewTrain(trainQueue, i - 1);
            //call method to create new passenger
            sim.createPassengers(stations, passengerQueue);
            //call method to move trains
            numPassengers = sim.moveTrains(stations, trainQueue, i);
        } // end for loop;
        Client.finalReport(Client.TIME_INTERVAL, Client.passengersDelivered, passengerQueue);
    }

}