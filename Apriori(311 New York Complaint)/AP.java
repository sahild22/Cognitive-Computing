	/*
	 ************  Ek Onkar  ************
	 * Association Rules we are targeting
	 * Noise Weekend, Noise Resedential -> No Permit	(Send NYPD when there is No Permit)
	 * Min Support  = 0.02 & Min Confidence = 0.8
	 ************************************
	 */
import java.util.*;
import java.text.*;
import java.io.*;

class AP{
	public static HashMap<String, Set<String>> suppDict = new HashMap<>();
	public static double minSupport;
	public static double minConfidence;
	public static double noOfRecords;
	public static int ms;
	public static boolean verboseFlag = false;
	public static boolean generateFlag = false;
	public static boolean masterFlag = false;
	public static boolean writFile = true;
	public static int noOfItems = 0;

	public static void main(String[] args) throws IOException{
		String input = "";
		try{
			input = args[0];
			//System.out.println(args[0]);
			if(input.equalsIgnoreCase("-v")){
				verboseFlag = true;
			}else  if(input.equalsIgnoreCase("-g")){
				generateFlag = true;
			}else if(input.equalsIgnoreCase("-m")){
				masterFlag = true;
			}
			minSupport = Double.parseDouble(args[1]);
			//System.out.println("Minimum Support: " + minSupport);
			minConfidence = Double.parseDouble(args[2]);
			//System.out.println("Minimum Confidence: " + minConfidence);
			//String newFile = args[2];
		}catch(Exception e){
			System.out.println(e);
		}	// flag check try catch ends

		HashMap<String, Set<String>> invMap = new HashMap<>();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		SimpleDateFormat newFormat = new SimpleDateFormat("EEEE");
		int index[] = {0,1,5,7,9};
		int index1[] = {0,2,6,7};
		String[] finalData = new String[8];
		StringBuilder sb = new StringBuilder();
		String day = "";

		if(generateFlag && writFile){
       		sb.append("Unique Key").append(",").append("Created Date").append(",").append("Complaint Type").append(",").append("Location Type").append(",").append("Incident Address").append(",").append("Day").append(",").append("Noise On Weekend").append(",").append("Permit");
       		System.out.println(sb.toString());
       		sb.setLength(0);
			writFile = false;
		}

		
		long startTime=System.nanoTime();
		InputStreamReader is = new InputStreamReader(System.in);
		try (BufferedReader br = new BufferedReader(is)) {
    		String line;
    		while((line = br.readLine()) != null){
    			String[] data = line.split(",");
    			if(!(data[0].equalsIgnoreCase("Unique key") || data[0].equalsIgnoreCase(""))){	
    				if(!data[1].equalsIgnoreCase("Created Date")){
    					noOfItems++;
    					Date date = (Date)formatter.parse(data[1]);
    					day = newFormat.format(date);
						//System.out.println("Final: " + finalString);
					}
					int x = 0;
					for(int j: index){
						finalData[x] = data[j];
						x++;
						if(generateFlag){
							sb.append(data[j]).append(",");
						}
					}
					finalData[5] = day;
					if((day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday") || day.equalsIgnoreCase("Friday")) && (data[5].equalsIgnoreCase("Noise - Residential") || data[5].equalsIgnoreCase("Noise - Commercial"))){
						finalData[6] = "Noise Weekend";
					}else{
						finalData[6] = "NA";
					}
					if(data[5].equalsIgnoreCase("Noise - Commercial") && data[7].equalsIgnoreCase("Club/Bar/Restaurant")){
						finalData[7] = "Permit";
					}else{
						finalData[7] = "No Permit";
					}
					if(generateFlag){
						sb.append(day).append(",").append(finalData[6]).append(",").append(finalData[7]);
						generate(sb.toString());
						sb.setLength(0);
					}

					for(int zz : index1){
						if(!finalData[zz].equalsIgnoreCase("NA")){
							if(invMap.containsKey(finalData[zz])){
								Set val = invMap.get(finalData[zz]);
								val.add(data[0]);
								invMap.put(finalData[zz], val);
							}else{
								Set<String> l = new HashSet<>();
								l.add(data[0]);
								invMap.put(finalData[zz], l);
							}
						}
					}
				}
			}//end while
			br.close();
			is.close();
		}catch(Exception e){
			System.out.println(e);
		}


/***************Calculate No. of Records and Minimum Support*******************/

		noOfRecords = (double)noOfItems;
		//System.out.println("No. of records: " + noOfRecords);
		ms = (int)(minSupport * noOfRecords);
		//System.out.println("Minimum support value: " + ms);

/****************	Step 1 Pruning		**************************************/

		Iterator iterator = invMap.keySet().iterator();
		Set<String> items = new HashSet<>();
		while (iterator.hasNext()) {
              	String key = iterator.next().toString();
              	Set value = invMap.get(key);
              	int length = value.size(); 
              	if(length >= ms){
              		// System.out.println("Not Pruned: " + key);
              		// System.out.println("MS: " + ms);
              		// System.out.println("Support: " + length);
              		items.add(key);
              	}
        }

        //System.out.println("Size Of Items: " + items.size());
        //System.out.println("Items: " + items);
		//System.out.println("Inverse Map size: " + invMap.size());

/****************	Support Dict. 	******************************************/

				for(String s : items){
					Set tempVal = invMap.get(s);
					suppDict.put(s, tempVal);
				}

			//System.out.println("SupportDict: " + suppDict.size());
			invMap.clear();

/***************		Creating set of Sets        **************************/
		
		HashSet<Set<String>> ss = new HashSet<>();
		for(String a : items){
			Set<String> tmp = new TreeSet();
			tmp.add(a);
			ss.add(tmp);
		}

		//System.out.println("SS is: " + ss);

/*****************		While Loop Begins		***************************/

		while(ss.size() > 1){
			HashSet<Set> tempSet = new HashSet<>();
			for(Set x : ss){
				for(Set y : ss){
					Set a = unionKey(x, y);
					if(a.size() != x.size()){
						tempSet.add(a);
					}
				}
			}
			ss.clear();
			// System.out.println("Set After Union: " + tempSet);
			// System.out.println("Size: " + tempSet.size());
			// System.out.println();
			for(Set se : tempSet){
				ss.add(se);
			}
			tempSet.clear();
			for(Set<String> z : ss){
				int supp = intersect(z);
				if(supp >= ms){
					tempSet.add(z);
					//System.out.println("Not Pruned: " + z);
				}
			}
			ss.clear();
			for(Set see : tempSet){
				ss.add(see);
			}
			//System.out.println("ss: " + ss);
		}
		rules(ss);
		long stopTime=System.nanoTime();
		double elapsedTime= (stopTime-startTime)/1e9;
		// System.out.println("Time taken: " + elapsedTime + " seconds");
		// //System.out.println("Final map:" + finalMap );
		//System.out.println("Final ss: " + ss);
	}//main ends


	public static void rules(HashSet<Set<String>> ss){
		HashSet<Set<String>> removeDuplicate = new HashSet<>();
		Set<String> subset = new HashSet<>();
		for(Set ab : ss){
			subset.addAll(ab);
		}
		double countFullSet = (double)intersect(subset);

		//System.out.println("Subset : " + subset);
		String[] arr = new String[subset.size()];
		int i = 0;
		for(String c : subset){
			arr[i] = c;
			i++;
		}

		for(int m = 0; m < arr.length; m++){
			for(int n = 0; n < arr.length; n++){
				Set<String> s1 = new HashSet<>();
				Set<String> s2 = new HashSet<>();
				Set<String> s3 = new HashSet<>();
				Set<String> s4 = new HashSet<>();
				
				for(String as : subset){
					s1.add(as);
				}
				s2.add(arr[m]);
				s3.add(arr[n]);
				s4 = unionKey(s2,s3);
				s1.removeAll(s4);
				//System.out.println(subset);
				//finalSet.add(s1);
				// finalSet1.add(s4);
				double leftSet = (double)intersect(s1);
				double conf = countFullSet/leftSet;
				if(s1.size() != 0){
					if((conf > minConfidence)){
						if(!removeDuplicate.contains(s1)){
							removeDuplicate.add(s1);
							if(verboseFlag){
								System.out.println(s1 + " -> " + s4 + " c: " + conf + " Not Pruned");
							}
							if(masterFlag){
								System.out.println(s1 + " -> " + s4 + " (sup = " + countFullSet/noOfRecords + " conf = " + conf);
							}
						}
					}else{
						if(verboseFlag){
							System.out.println(s1 + " -> " + s4 + " c: " + conf + " Pruned");
						}
					}   
				} 
			}
		}
	}

	public static void generate(String s){
		System.out.println(s);
	}

	public static Set<String> unionKey(Set a, Set b){
		Set<String> union = new TreeSet<>();
		union.addAll(a);
		union.addAll(b);
		return union;
	}

	public static int intersect(Set<String> a){
		if(a.size() != 0){
			int length = a.size();
			Set<String> inter = new TreeSet<>();
			//System.out.println("Lengtth: " + length);
				String[] arr = new String[length];
				int i = 0;
				for(String s : a){
					arr[i] = s;
					i++;
				}
				//System.out.println("Array is: "+arr[0] + " : " + arr[1]);
				inter.addAll(suppDict.get(arr[0]));
				for(int j = 1; j < length; j++){
					inter.retainAll(suppDict.get(arr[j]));
				}
			return inter.size();
		}else{
			return 0;
		}
	}
}//class end
