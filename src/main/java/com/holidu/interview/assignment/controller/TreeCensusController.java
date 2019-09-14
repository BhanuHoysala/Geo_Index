package com.holidu.interview.assignment.controller;

import com.holidu.interview.assignment.exception.NotFoundException;
import com.holidu.interview.assignment.exception.ValidationException;
import com.holidu.interview.assignment.service.TreeCensusService;
import com.javadocmd.simplelatlng.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/treeCensus")
public class TreeCensusController {

    @Autowired
    private TreeCensusService treeCensusService;

    @GetMapping("/count")
    public Response getTreeCount(@QueryParam("lat") double xPoint,
                                 @QueryParam("lon") double yPoint,
                                 @QueryParam("radius") int radius) {

        try {

            validateParameters(xPoint, yPoint, radius);

            LatLng latLng = new LatLng(xPoint, yPoint);
            Map<String, Long> unsortedTreeCount = treeCensusService.getTreeCount(latLng, radius);

            // Sorting Map based on Tree name
            Map<String, Long> sortedTreeCount = new LinkedHashMap<>(unsortedTreeCount.size());
            unsortedTreeCount.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedTreeCount.put(x.getKey(), x.getValue()));

            return Response.status(Response.Status.OK).entity(sortedTreeCount).build();
        } catch (ValidationException ve) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ve.getMessage()).build();
        } catch (NotFoundException nfe) {
            log.info(nfe.getMessage());
            return Response.status(Response.Status.OK).entity(nfe.getMessage()).build();
        }
    }

    private void validateParameters(double latitude, double longitude, int radius) throws ValidationException {

        if (!(latitude >= -90 && latitude <= 90)) {
            throw new ValidationException("Invalid latitude search parameter " + latitude);
        }

        if (!(longitude >= -180 && longitude <= 180)) {
            throw new ValidationException("Invalid longitude search parameter " + longitude);
        }

        if (!(radius >= 1)) {
            throw new ValidationException("Invalid radius search parameter " + radius);
        }
    }
}
