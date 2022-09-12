import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

  static List<List<Passenger>> floors = new ArrayList<>();
  static final int maxFloor = new Random().nextInt(5, 20); // Рандомна кі-ть поверхів
  static int currentLiftFloor = 0; // Початковий поверх 0

  public static void main(String[] args) throws InterruptedException {
    setPassengersOnFloors(maxFloor);
    new Lift().move();
  }

  // Заповнюємо поверхи пасажирами
  public static void setPassengersOnFloors(int maxFloor) {
    for (int i = 0; i < maxFloor; i++) {
      floors.add(setPassengers(i, maxFloor));
    }
  }

  // Заповнюємо 1 поверх пасажирами
  public static List<Passenger> setPassengers(int currentFloor, int maxFloors) {
    List<Passenger> passengers = new ArrayList<>();
    for (int i = 0; i <= new Random().nextInt(0, 10); i++) {
      passengers.add(new Passenger(currentFloor));
    }
    return passengers;
  }

  // Клас для пасажира
  private static class Passenger{
    int destination;
    int currentFloor;

    public Passenger(int currentFloor) {
      this.currentFloor = currentFloor;
      destination = getRandomWithExclusion(currentFloor);
    }

    public void setCurrentFloor(int currentFloor) {
      this.currentFloor = currentFloor;
    }

    public int getDestination() {
      return destination;
    }

    public void setDestination() {
      destination = getRandomWithExclusion(currentFloor);
    }

    // Визначаємо рандомне число виключаючи теперішній поверх
    public int getRandomWithExclusion(int currentFloor) {
      int random = new Random().nextInt(0, maxFloor);
      while (random == currentFloor) {
        random = new Random().nextInt(0, maxFloor);
      }
      return random;
    }
  }

  private static class PassengerComparator implements Comparator<Passenger> {

    @Override
    public int compare(Passenger o1, Passenger o2) {
      return Integer.compare(o1.destination, o2.destination);
    }
  }

  private static class Lift {
    private static final int capacity = 5;
    private List<Passenger> passengers = new ArrayList<>();
    private List<Passenger> arrivedPassengers = new ArrayList<>();
    private boolean movingUp = true;
    int liftDestination = 0;
    Passenger maxDestinationPassenger;

    public void move() throws InterruptedException {
      while (true) {
        displayInfo();
        deliverPassenger();
        pickupPassenger();
        setNewDestinationForArrivedPassengers();
        goToTheNextFloor();
        System.out.println("NEXT STAGE");
        Thread.sleep(1000);
      }

    }

    public void deliverPassenger() {
      if (!passengers.isEmpty()) {
        arrivedPassengers = passengers.stream().
            filter(p -> p.currentFloor == p.destination)
            .collect(Collectors.toList());
        passengers.removeAll(arrivedPassengers);
      }
    }

    public void setNewDestinationForArrivedPassengers() {
      if (!arrivedPassengers.isEmpty()) {
        for (Passenger arrivedPassenger : arrivedPassengers) {
          arrivedPassenger.setDestination();
        }
        for (Passenger passenger : arrivedPassengers) {
          floors.get(currentLiftFloor).add(passenger);
        }
        arrivedPassengers.clear();
      }
    }

    public void pickupPassenger() {
      if (passengers.size() <= capacity) {
        int leftSpace = capacity - passengers.size();
        List<Passenger> passengersOnCurrentFloor = floors.get(currentLiftFloor);
        if (movingUp) {
          Collections.sort(passengersOnCurrentFloor, new PassengerComparator().reversed());
        } else {
          Collections.sort(passengersOnCurrentFloor, new PassengerComparator());
        }
        for (int i = 0; i < leftSpace && i < passengersOnCurrentFloor.size(); i++) {
          passengers.add(passengersOnCurrentFloor.get(i));
        }
        Collections.sort(passengers, new PassengerComparator());
        floors.get(currentLiftFloor).removeAll(passengers);
        setMaxDestination();
      }
    }

    public void goToTheNextFloor() {
      if (movingUp || currentLiftFloor == 0) {
        currentLiftFloor++;
      } else if (!movingUp || currentLiftFloor == maxFloor){
        currentLiftFloor--;
      }
      for (Passenger currentPassenger : passengers) {
        currentPassenger.setCurrentFloor(currentLiftFloor);
      }
    }

    public void setMaxDestination() {
      if (movingUp) {
        maxDestinationPassenger = Collections.max(passengers, new PassengerComparator());
      } else {
        maxDestinationPassenger = Collections.min(passengers, new PassengerComparator());
      }

      liftDestination = maxDestinationPassenger.destination;

      if (liftDestination > currentLiftFloor && currentLiftFloor <= maxFloor) {
        movingUp = true;
      }
      else {
        movingUp = false;
      }
//      if (maxDestinationPassenger == null && !passengers.isEmpty()) {
//        maxDestinationPassenger = Collections.max(passengers, new PassengerComparator());
//      } else {
//
//      }
//      if (maxDestinationPassenger.destination > currentLiftFloor) {
//        maxDestination = maxDestinationPassenger.destination;
//        movingUp = true;
//      }
//      else {
//        maxDestination = maxDestinationPassenger.destination;
//        movingUp = false;
//      }
//        for (Passenger passenger : passengers) {
//          if (passenger.destination > maxDestination) {
//            maxDestination = passenger.destination;
//            movingUp = true;
//          } else {
//            maxDestination = passenger.destination;
//            movingUp = false;
//          }
//        }
    }

//    public boolean isAllArrived() {
//      if (currentLiftFloor == maxDestination) {
//        return true;
//      } else {
//        return false;
//        }
//    }

    private void displayInfo(){
      for (int i = 0; i < maxFloor; i++) {
        StringBuilder floorDisplay = new StringBuilder();
        StringBuilder liftDisplay = new StringBuilder();
        floorDisplay.append("Floor " + (i+1) + "| Passengers: ");
        liftDisplay.append("| Lift: ");
        List<Passenger> currentFloor = floors.get(i);
        if (!currentFloor.isEmpty()) {
          for (Passenger passenger : currentFloor) {
            floorDisplay.append(passenger.destination+1 + " ");
          }
          floorDisplay.append("|\t");
        } else {
          floorDisplay.append("NONE");
        }
        if (!passengers.isEmpty()) {
          for (Passenger passenger : passengers) {
            liftDisplay.append(passenger.destination+1 + " ");
          }
          liftDisplay.append("| ");
        } else {
          liftDisplay.append("| NONE | ");
        }
        if (movingUp) {
          liftDisplay.append("Going UP");
        } else {
          liftDisplay.append("Going DOWN");
        }
        System.out.print(floorDisplay);
        if (i == currentLiftFloor) {
          System.out.print(liftDisplay);
        }
        System.out.println();
      }
    }
  }
}
