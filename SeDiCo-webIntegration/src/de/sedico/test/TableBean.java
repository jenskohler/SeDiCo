package de.sedico.test;

import java.io.Serializable;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.UUID;  

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;


import org.primefaces.event.DragDropEvent;
 
@ManagedBean(name = "tableBean")
@SessionScoped
/**
 * In dieser Klasse wird eine Tabelle getestet. Sie implementiert das Interface Serializable.
 * @author jens
 *
 */
public class TableBean implements Serializable {  
	private static final long serialVersionUID = 1L;

	private final static String[] colors;  
	  
    private final static String[] manufacturers;  
  
    private List<Car> carsSmall;  
  
    private List<Car> droppedCars;  
    static {  
        colors = new String[10];  
        colors[0] = "Black";  
        colors[1] = "White";  
        colors[2] = "Green";  
        colors[3] = "Red";  
        colors[4] = "Blue";  
        colors[5] = "Orange";  
        colors[6] = "Silver";  
        colors[7] = "Yellow";  
        colors[8] = "Brown";  
        colors[9] = "Maroon";  
  
        manufacturers = new String[10];  
        manufacturers[0] = "Mercedes";  
        manufacturers[1] = "BMW";  
        manufacturers[2] = "Volvo";  
        manufacturers[3] = "Audi";  
        manufacturers[4] = "Renault";  
        manufacturers[5] = "Opel";  
        manufacturers[6] = "Volkswagen";  
        manufacturers[7] = "Chrysler";  
        manufacturers[8] = "Ferrari";  
        manufacturers[9] = "Ford";  
    }  
  
    
  
    public TableBean() {  
        carsSmall = new ArrayList<Car>();  
        droppedCars = new ArrayList<Car>();  
  
        populateRandomCars(carsSmall, 9);  
    }  
    
  /**
   * Diese Methode fügt einer Liste von Autos zufällige Autos hinzu.
   * @param list - Liste von Autos
   * @param size - Größe
   */
    private void populateRandomCars(List<Car> list, int size) {  
        for(int i = 0 ; i < size ; i++)  
            list.add(new Car(getRandomModel(), getRandomYear(), getRandomManufacturer(), getRandomColor()));  
    }  
  
    public List<Car> getCarsSmall() {  
        return carsSmall;  
    }  
  
    private int getRandomYear() {  
        return (int) (Math.random() * 50 + 1960);  
    }  
  
    private String getRandomColor() {  
        return colors[(int) (Math.random() * 10)];  
    }  
  
    private String getRandomManufacturer() {  
        return manufacturers[(int) (Math.random() * 10)];  
    }  
  
    private String getRandomModel() {  
        return UUID.randomUUID().toString().substring(0, 8);  
    }  

    public void onCarDrop(DragDropEvent ddEvent) {  
    	System.out.println("TableBean: onCarDrop");
    	Car car = ((Car) ddEvent.getData());  
  
        droppedCars.add(car);  
        carsSmall.remove(car);  
        
        System.out.println("TableBean: onCarDrop");
    }  
  
    public List<Car> getDroppedCars() {  
        return droppedCars;  
    }  
}  