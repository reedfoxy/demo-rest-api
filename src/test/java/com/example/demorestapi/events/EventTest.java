package com.example.demorestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("REST API")
                .description("description")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String name = "Event";
        event.setName(name);
        String description = "Spring";
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    @Parameters(method = "parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        event.update();
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestFree() {
        return new Object[][] {
                new Object[] {0, 0, true},
                new Object[] {100, 0 , false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters(method = "parametersForTestOffline")
    public void testOffline(String location, boolean isOffline) {
        Event event = Event.builder()
                .location(location)
                .build();
        event.update();
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] parametersForTestOffline() {
        return new Object[][] {
                new Object[] {"location", true},
                new Object[] {null, false},
                new Object[] {"     ", false}
        };
    }

}