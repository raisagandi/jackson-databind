package com.fasterxml.jackson.databind.jdk17;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Java17CollectionsMapTest
{
    static class Employee {
        String id;
        String name;

        public Employee(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    static List<Employee> employees;
    static Map<String, String> employeeIdToName;
    static ObjectMapper MAPPER;
    static String idToNameMappingJson;
    static Map<?,?> deserializedResult;
    static final String EMPLOYEE_ID_1 = "1";
    static final String EMPLOYEE_ID_2 = "2";
    static final String EMPLOYEE_ID_3 = "3";

    @BeforeAll
     static void setUp() throws JsonProcessingException {
        MAPPER = JsonMapper.builder()
                .activateDefaultTypingAsProperty(
                        new NoCheckSubTypeValidator(),
                        ObjectMapper.DefaultTyping.EVERYTHING,
                        "@class"
                ).build();

        employees = Stream.of(
                new Employee(EMPLOYEE_ID_1, "Anna Drake"),
                new Employee(EMPLOYEE_ID_2, "Ben Thomas"),
                new Employee(EMPLOYEE_ID_3, "Cathy Mason")
        ).toList();

        employeeIdToName =
                employees.stream().collect(Collectors.toMap(Employee::getId,
                        Employee::getName));

        idToNameMappingJson = MAPPER.writeValueAsString(employeeIdToName);

        deserializedResult = MAPPER.readValue(idToNameMappingJson,
                Map.class);
    }

    @Test
    public void testMapSizesAreEqual()
    {
        assertEquals(employeeIdToName.size(), deserializedResult.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPLOYEE_ID_1, EMPLOYEE_ID_2,
            EMPLOYEE_ID_3})
    public void testResultMapContainsSameKeys(String id)
    {
        assertEquals(employeeIdToName.containsKey(id),
                deserializedResult.containsKey(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {EMPLOYEE_ID_1, EMPLOYEE_ID_2,
            EMPLOYEE_ID_3})
    public void testResultMapContainsSameValues(String id)
    {
        assertEquals(employeeIdToName.get(id), deserializedResult.get(id));
    }
}
