package com.holidu.interview.assignment.service;

import com.holidu.interview.assignment.exception.NotFoundException;
import com.javadocmd.simplelatlng.LatLng;

import java.util.Map;

public interface TreeCensusService {

    Map<String, Long> getTreeCount(LatLng cartesianPoints, int radius ) throws NotFoundException;
}
