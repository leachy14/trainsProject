import java.util.ArrayList;
import java.util.Random;

/**
 * Client program to handle the train simulation functions
 * @author Christopher Leach
 * @version 0.1
 */
public class Client {
    //Global variables
    public static Random generator = new Random();
    public static final int NUM_STATIONS = 5;
    public static final int TRAIN_CAPACITY = 50;
    public static final int TRAIN_INTERVAL = 10;
    public static final int TIME_INTERVAL = 200;
    public static int trainCount = 0;
    public static int passengerOnTrains = 0;
    public static int passengersDelivered;


    public void createStations(ArrayList<Station> stations) {
        // i'll create 5 stations
        for (int i = 0; i < NUM_STATIONS; i++) {
            //generate amount of time until next station, randomly (interval 5-14)
            int timeToNext = generator.nextInt(10) + 5;
            //create station
            Station station = new Station(timeToNext);
            //add station to list of stations
            stations.add(station);
            // output station has been created and include the time to next station
            System.out.println("Station " + i + " has been created with a time to next station of " + timeToNext);
        }
    } // end createStations

    public void startNewTrain(QueueInterface<Train> trainQueue, int time) {
        //check to ensure if time is divisible by train interval
        if (time % TRAIN_INTERVAL == 0) {
            //create new train
            Train train = new Train(TRAIN_CAPACITY);
            //add train to queue
            trainQueue.enqueue(train);
            //count up traincount
            trainCount++;
            //output train has been created
            System.out.println("Train " + trainCount + " has been created");
        }
    } //end startNewTrain

    public void createPassengers(ArrayList<Station> stations, QueueInterface<Passenger> passengerQueue) {
        //generate a random number between 0-5 to determine how many passengers to create
        int numPassengers = generator.nextInt(6);
        //loop through number of passengers to create
        for (int i = 0; i < numPassengers; i++) {
            //initialize start and stop stations
            int startStation = 0;
            int stopStation = 0;
            //generate random start station
            startStation = generator.nextInt(NUM_STATIONS);
            System.out.println("Passenger " + i + " has been created at station " + startStation);
            //generate random stop station
            stopStation = generator.nextInt(NUM_STATIONS);
            //loop until start station is less than stop station, if not already
            if (startStation >= stopStation) {
                for (int j = 0; j < NUM_STATIONS; j++) {
                    if (startStation < stopStation) {
                        break;
                    } else {
                        stopStation = generator.nextInt(NUM_STATIONS);
                    }
                }
            }
            //create passenger
            Passenger passenger = new Passenger(startStation, stopStation);
            //add passenger to appropriate station
            stations.get(startStation).addPassenger(passenger);
            // add passenger to queue
            passengerQueue.enqueue(passenger);
        } // end loop
    } // end createPassengers

    public int moveTrains(ArrayList<Station> stations, QueueInterface<Train> trainQueue, int time) {
        //store trainCount variable in local variable for numTrains
        int numTrains = trainCount;
        //loop through number of trains
        for (int i = 0; i < numTrains; i++) {
            //get a train from the queue
            Train train = trainQueue.getFront();
            //move the train
            train.move();
            System.out.println("Train " + i + " has moved to station " + train.nextStation());
            //get time to next station
            int timeToNext = train.timeToNext();
            System.out.println("Time to next station: " + timeToNext);
            //check to see if train is at a station
            if (timeToNext <= 0) {
                //get current station number
                int currentStation = train.nextStation();
                //get station from list of stations
                Station station = stations.get(currentStation);
                // unload passengers to station
                int leavingPassengers = train.unloadPassengers(currentStation);
                // load passengers from station
                int addedPassengers = train.loadPassengers(station, time);
                //update passengers on trains
                passengerOnTrains += addedPassengers;
                passengersDelivered += leavingPassengers;
                System.out.println("Passengers delivered: " + passengersDelivered);
                //update station
                train.updateStation(timeToNext);
                //check to see if train is at the last station
                if (currentStation == NUM_STATIONS - 1) {
                    //remove train from queue
                    trainQueue.dequeue();
                    //output train has been removed
                    System.out.println("Train " + i + " has been removed.");
                } else {
                    //add train back to queue
                    trainQueue.enqueue(train);
                }
            } // end if
        }// end for loop
        return passengerOnTrains;
    } // end moveTrains


    /** Reports the final situations of the trains and passengers waiting
     and some statistics for passengers' wait times.
     @param clock The time that train operations have ceased. */
    public static void finalReport(int clock, int passengersCreated,
                                   QueueInterface<Passenger> passengers)
    {
        System.out.println("Final Report");
        System.out.println("The total number of passengers is " +
                passengersCreated);
        System.out.println("The number of passengers currently on a train " +
                passengerOnTrains );
        System.out.println("The number of passengers delivered is " +
                passengersDelivered);
        int waitBoardedSum = 0;
        int waitNotBoardedSum = 0;
        for (int i=0; i < passengersCreated; i++)
        {
            Passenger p = passengers.dequeue();
            if(p.boarded())
                waitBoardedSum += p.waitTime(clock);
            else
                waitNotBoardedSum += p.waitTime(clock);
        } // end for
        System.out.println("The average wait time for passengers " +
                "that have boarded is");
        System.out.println((double)waitBoardedSum/(passengerOnTrains +
                passengersDelivered));
        System.out.println("The average wait time for passengers " +
                "that have not yet boarded is");
        System.out.println((double)waitNotBoardedSum /
                (passengersCreated - passengerOnTrains -passengersDelivered));
    } // end finalReport
}
