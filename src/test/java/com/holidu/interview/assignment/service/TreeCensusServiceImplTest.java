package com.holidu.interview.assignment.service;

import com.holidu.interview.assignment.exception.NotFoundException;
import com.javadocmd.simplelatlng.LatLng;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TreeCensusServiceImplTest {

    @Autowired
    private TreeCensusServiceImpl treeCensusService;

    private LatLng latLng = new LatLng(40.72309177, -73.84);

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("api.nyc.open.data.tree", "https://data.cityofnewyork.us/resource/nwxe-4ae8.json");
    }

    /**
     * Valid scenarios where trees can be found for the valid coordinates and radius
     */
    @Test
    public void getTreeCountPositive() throws NotFoundException {

        Map<String, Long> treeCounter = treeCensusService.getTreeCount(latLng, 1000);
        Assert.assertNotNull(treeCounter);
        Assert.assertFalse(treeCounter.isEmpty());
    }

    /**
     * When no trees found for the given radius
     *
     * @throws NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void getTreeCountNegative() throws NotFoundException {

        treeCensusService.getTreeCount(latLng, 1);
    }

}