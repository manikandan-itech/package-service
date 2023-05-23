package com.mobiquity.packer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mobiquity.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Packer {
	
	private static final String ITEM_INDEX = "index";
    private static final String WEIGHT = "weight";
    private static final String COST = "cost";
    private static final String PACKAGE_REGEX = "\\((?<" + ITEM_INDEX + ">\\d+)\\,(?<" + WEIGHT + ">\\d+(\\.\\d{1,2})?)\\,€(?<" + COST + ">\\d+(\\.\\d{1,2})?)\\)";
    private static final int MAX_ITEMS_IN_PACKAGE = 15;
    private static final int MAX_WEIGHT = 100 * 100;
    private static final int MAX_COST = 100;
	
	public static String pack(String filePath) throws IOException {
		
		 var packageList = new ArrayList<Package>();
         try (var out = new BufferedReader(
                  new InputStreamReader(new FileInputStream(filePath)))) {
              String line1;
              while ((line1 = out.readLine()) != null) {
                   packageList.add(fetchPackages(line1));
              }
              out.close();
          } catch(ApiException e) {
            throw e;
         } 
		 return packageList.stream().map(pack -> getCombinationAsString(pack.getWeight(), pack.getItems())).collect(Collectors.joining("\n"));
	}
	
    public static String pack(byte[] fileContent) throws IOException {
    	
    	var packageList = new ArrayList<Package>();
		
		try(var in = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(fileContent)))){
            String line;
            
            while ((line = in.readLine()) != null) {
            	packageList.add(fetchPackages(line));
            }
            in.close();
		} catch(ApiException e) {
        	throw e;
        } 
		  
		return packageList.stream().map(pack -> getCombinationAsString(pack.getWeight(), pack.getItems())).collect(Collectors.joining("\n"));
	}
	
	private static Package fetchPackages(String packageDetail) {
		
		try {
		
			var packageLine = packageDetail.split(":");
			
			var packageWeight = Integer.valueOf(packageLine[0]) * 100;
			var packageItems = packageLine[1];
			
			var pattern = Pattern.compile(PACKAGE_REGEX);
			var itemsFromLine = pattern.matcher(packageItems);
			
			var items = new ArrayList<Item>();
			
			while(itemsFromLine.find()) {
				
				var index = Integer.valueOf(itemsFromLine.group(ITEM_INDEX));
	            var weight = (int) (Double.valueOf(itemsFromLine.group(WEIGHT)) * 100);
	            var cost = Integer.valueOf(itemsFromLine.group(COST));
	            
	            if(makeSureItemIsEligibleToAddInAPackage(index, weight, cost)) {
		            addItem(items, index, weight, cost);
	            } else {
	            	log.info("Item {} is not eleigible to add to a package ! " , index);
	            }
				
			}
			
			return addItemsToPackage(packageWeight, items);
			
		} catch(NumberFormatException e) {
			throw new ApiException("Incorrect package line ::" +packageDetail);
		}
		
	}
	
	private static String getCombinationAsString(int maxWeight, List<Item> packages) {
		
        int packageSize = packages.size() + 1;
        int packageMaxWeight = maxWeight + 1;
        double[][] a = new double[packageSize][packageMaxWeight];
        
        
        if( maxWeight < MAX_WEIGHT) {
	        for (int i = 1; i < packageSize; i++) {
	            Item pack = packages.get(i - 1);
	
	            for (int j = 1; j < packageMaxWeight; j++) {
	            	
	                if (pack.getWeight() > j) {
	                    a[i][j] = a[i - 1][j];
	                } else {
	                    a[i][j] = Math.max(a[i - 1][j], a[i - 1][j - pack.getWeight()] + pack.getCost());
	                }
	            }
	        }
        } else {
        	log.info("Package weight is exceeds the maxmimun limit !");
        }
        	

        var itemIndexes = new ArrayList<Integer>();
        int j = maxWeight;

        for (int i = packageSize - 1; i > 0; i--) {
            if (a[i][j] != a[i - 1][j]) {
                itemIndexes.add(packages.get(i - 1).getIndex());
                j -= packages.get(i - 1).getWeight();
            }
        }

        var packageResult =
                itemIndexes.stream()
                        .mapToInt(i -> i)
                        .sorted()
                        .mapToObj(index -> Integer.toString(index))
                        .collect(Collectors.joining(","));
        return packageResult.isEmpty() ? "-" : packageResult;
    }


	private static void addItem(ArrayList<Item> items, Integer index, int weight, Integer cost) {
		items.add(Item.builder()
				.index(index)
				.weight(weight)
				.cost(cost)
				.build());
	}

	private static Package addItemsToPackage(int packageWeight, ArrayList<Item> items) {
		return Package.builder()
				.weight(packageWeight)
				.items(items)
				.build();
	}

	private static boolean makeSureItemIsEligibleToAddInAPackage(Integer index, int weight, Integer cost) {
		return makeSureItemIndexShouldNotExceedMax(index) && 
				itemWeightShouldNotExceedMax(weight) && 
				itemCostShouldNotExceedMax(cost);
	}

	private static boolean itemCostShouldNotExceedMax(Integer cost) {
		return MAX_COST >= cost || cost < 0;
	}

	private static boolean itemWeightShouldNotExceedMax(int weight) {
		return MAX_WEIGHT >= weight || weight < 0;
	}

	private static boolean makeSureItemIndexShouldNotExceedMax(Integer index) {
		return MAX_ITEMS_IN_PACKAGE >= index || index < 0;
	}

}
