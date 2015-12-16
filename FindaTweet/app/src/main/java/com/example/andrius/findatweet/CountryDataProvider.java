package com.example.andrius.findatweet;

/**
 * Created by andrius on 2015-12-11.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CountryDataProvider {

    public static HashMap<String, List<String>> getDataHashMap() {
        HashMap<String, List<String>> countriesHashMap = new HashMap<String, List<String>>();

        List<String> SwedenList = new ArrayList<String>();
       List<String> USAList = new ArrayList<String>();
        List<String> EuropeList = new ArrayList<String>();

        for (int i = 0; i < CountryDataArrays.SwedenArray.length; i++) {
            SwedenList.add(CountryDataArrays.SwedenArray[i]);
        }

        for (int i = 0; i < CountryDataArrays.USAArray.length; i++) {
           USAList.add(CountryDataArrays.USAArray[i]);
       }

        for (int i = 0; i < CountryDataArrays.EuropeArray.length; i++) {
           EuropeList.add(CountryDataArrays.EuropeArray[i]);
       }

        countriesHashMap.put( "Sweden" ,SwedenList);
        countriesHashMap.put("USA", USAList);
       countriesHashMap.put("Europe", EuropeList);

        return countriesHashMap;
    }
}