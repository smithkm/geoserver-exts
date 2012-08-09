package org.opengeo.data.csv;

import org.opengeo.data.csv.parse.CSVSpecifiedLatLngStrategy;
import org.opengeo.data.csv.parse.CSVStrategy;

public class CSVSpecifiedLatLngStrategyFactory implements CSVStrategyFactory {

    private final CSVFileState csvFileState;
    private final String lat;
    private final String lng;

    public CSVSpecifiedLatLngStrategyFactory(CSVFileState csvFileState, String lat, String lng) {
        this.csvFileState = csvFileState;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public CSVStrategy createCSVStrategy() {
        return new CSVSpecifiedLatLngStrategy(csvFileState, lat, lng);
    }

}
