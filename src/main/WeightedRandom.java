/**
 * @author Philipp from gamedev.stackexchange.com + some modification from Ethan Gan
 * Computer Science
 * 1/23/2023: UNUSED IN NEW VERSION
 * WeightedRandom class manages selecting random options with weights
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Weighted Random class chooses options based on weights, source: https://gamedev.stackexchange.com/questions/162976/how-do-i-create-a-weighted-collection-and-then-pick-a-random-element-from-it */
public class WeightedRandom<Integer extends Object>{
	/** Entry class containing the weight and the integer object */
    private class Entry implements Comparable{
        double accumulatedWeight;
        Integer object;
    	@Override
    	public int compareTo(Object o) {
    		if (this.accumulatedWeight > ((Entry) (o)).accumulatedWeight) {
    			return 1;
    		}
    		else {
    			return 0;
    		}
    	}
    }
    private List<Entry> entries = new ArrayList<Entry>();
    private double accumulatedWeight;
    private Random rand = new Random();
	
    /** 
     * Method adds new entries to the weighted random class
	 * pre: an integer object and weight
	 * post: a new entry stored inside the weighted random class
	 */
    public void addEntry(Integer object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        
        //sort entry list items to optimize speed with the most likely objects first
        Collections.sort(entries);  
        entries.add(e);
    }

    /** 
     * Method gets random entry value to return using random and weights
	 * pre: entries.size() > 0
	 * post: an integer object returned
	 */
    public int getRandom() {
    	if (entries.size() > 0) {
            double r = rand.nextDouble() * accumulatedWeight;

            for (Entry entry: entries) {
                if (entry.accumulatedWeight >= r) {
                    return (int) entry.object;
                }
            }    		
    	}
        return 999; //should only happen when there are no entries
    }

    /** 
     * Method allows for the comparison of different weighted random entries based on their weights
   	 * pre: two entries that are not null
   	 * post: a difference of their weight values to determine which one should be ahead in the entry list
   	 */
	public double compareTo(Entry entry1, Entry entry2) {
		double value1 = entry1.accumulatedWeight;
		double value2 = entry2.accumulatedWeight;
		
		return value1-value2;
	}

}