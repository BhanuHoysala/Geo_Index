package com.holidu.interview.assignment.service;

import com.holidu.interview.assignment.exception.NotFoundException;
import com.holidu.interview.assignment.model.TreeCensus;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ferriludium.simplegeoprox.FeSimpleGeoProx;
import org.ferriludium.simplegeoprox.MapObjectHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("treeCensusService")
public class TreeCensusServiceImpl implements TreeCensusService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.nyc.open.data.tree}")
    private String treeCensusApiUrl;

    private static List<TreeCensus> treeCensusesCached = new ArrayList<>();

    @Override
    public Map<String, Long> getTreeCount(LatLng latLng, int radius) throws NotFoundException {

        List<TreeCensus> treeCensusList = fetchTreeCensusData();

        List<MapObjectHolder<TreeCensus>> mapObjects = new ArrayList<>();

        treeCensusList.parallelStream().filter(t -> StringUtils.isNotBlank(t.getTreeName())).forEach(t ->
                mapObjects.add(new MapObjectHolder<TreeCensus>(new LatLng(t.getLatitude(), t.getLongitude()), t))
        );

        FeSimpleGeoProx<TreeCensus> allTreeFromCensus = new FeSimpleGeoProx<>(mapObjects);
        LatLng start = new LatLng(latLng.getLatitude(), latLng.getLongitude());
        Set<MapObjectHolder<TreeCensus>> treesFoundInRadius =
                (HashSet<MapObjectHolder<TreeCensus>>) allTreeFromCensus.find(start, radius, LengthUnit.METER);

        if (CollectionUtils.isEmpty(treesFoundInRadius)) {
            throw new NotFoundException("Could not find any trees for given search criteria");
        }

        Map<String, Long> treeCommonNameCounterMap = treesFoundInRadius.parallelStream()
                .collect(Collectors.groupingBy(e -> e.clientObject.getTreeName(), Collectors.counting()));

        log.info("Total trees found in the given radius is {}", treesFoundInRadius.size());

        return treeCommonNameCounterMap;
    }

    @HystrixCommand(fallbackMethod = "fetchCachedTreeCensusData")
    public List<TreeCensus> fetchTreeCensusData() {

        TreeCensus[] responseEntity = restTemplate.getForObject(treeCensusApiUrl, TreeCensus[].class);
        log.info("Total trees found from the API is {}", responseEntity.length);
        treeCensusesCached = Arrays.asList(responseEntity);
        return treeCensusesCached;
    }

    public List<TreeCensus> fetchCachedTreeCensusData() {
        log.info("Fetching Tree census data from cache");
        return treeCensusesCached;
    }
}
