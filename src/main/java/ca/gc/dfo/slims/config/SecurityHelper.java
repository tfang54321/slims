package ca.gc.dfo.slims.config;

import ca.gc.dfo.spring_commons.commons_offline_wet.security.EAccessSecurityHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityHelper extends EAccessSecurityHelper
{
    private static final String EL_HAS_ROLE_OPEN = "hasRole(";
    private static final String EL_HAS_ANY_ROLE_OPEN = "hasAnyRole(";
    private static final String EL_HAS_ROLE_CLOSE = ")";

    public static final String ROLE_GUEST = "GUEST";
    public static final String ROLE_STUDENT_TRAINEE = "STUDENT_TRAINEE";
    public static final String ROLE_CONTRIBUTOR = "CONTRIBUTOR";
    public static final String ROLE_ADMIN = "ADMIN";

    //AUTHORITY GRANT CONSTANTS
    public static final String GRANT_GUEST = "'" + ROLE_GUEST + "'";
    public static final String GRANT_STUDENT_TRAINEE = "'" + ROLE_STUDENT_TRAINEE + "'";
    public static final String GRANT_CONTRIBUTOR = "'" + ROLE_CONTRIBUTOR + "'";
    public static final String GRANT_ADMIN = "'" + ROLE_ADMIN + "'";

    //VIEW - ROLE SPEL
    public static final String EL_VIEW_USER_MANAGEMENT = EL_HAS_ANY_ROLE_OPEN + GRANT_STUDENT_TRAINEE + "," +
            GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_VIEW_CODE_TABLES = EL_VIEW_USER_MANAGEMENT; //Same rights, so re-use
    public static final String EL_VIEW_LOCATIONS = EL_HAS_ANY_ROLE_OPEN + GRANT_GUEST + "," + GRANT_STUDENT_TRAINEE +
            "," + GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_VIEW_ADULT_SPAWNING = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_FISH_COLLECTIONS = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_DEPLETION_STUDIES = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_HABITAT_INVENTORY = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_LARVAL_ASSESSMENTS = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_PARASITIC_COLLECTIONS = EL_VIEW_LOCATIONS; //Same rights, so re-use
    public static final String EL_VIEW_TREATMENTS = EL_VIEW_LOCATIONS; //Same rights, so re-use

    //MODIFY (ADD/UPDATE) - ROLE SPEL
    public static final String EL_MODIFY_USER_MANAGEMENT = EL_HAS_ROLE_OPEN + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_MODIFY_CODE_TABLES = EL_MODIFY_USER_MANAGEMENT; //Same rights, re-use
    public static final String EL_MODIFY_LOCATIONS = EL_HAS_ANY_ROLE_OPEN + GRANT_CONTRIBUTOR + "," +
            GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_MODIFY_ADULT_SPAWNING = EL_HAS_ANY_ROLE_OPEN + GRANT_STUDENT_TRAINEE + "," +
            GRANT_CONTRIBUTOR + "," + GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_MODIFY_BARRIER = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_FISH_COLLECTIONS = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_DEPLETION_STUDIES = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_HABITAT_INVENTORY = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_LARVAL_ASSESSMENTS = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_PARASITIC_COLLECTIONS = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use
    public static final String EL_MODIFY_TREATMENTS = EL_MODIFY_ADULT_SPAWNING; //Same rights, re-use

    //DELETE - ROLE SPEL
    public static final String EL_DELETE_LOCATIONS = EL_HAS_ANY_ROLE_OPEN + GRANT_CONTRIBUTOR + "," +
            GRANT_ADMIN + EL_HAS_ROLE_CLOSE;
    public static final String EL_DELETE_ADULT_SPAWNING = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_BARRIER = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_FISH_COLLECTIONS = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_DEPLETION_STUDIES = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_HABITAT_INVENTORY = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_LARVAL_ASSESSMENTS = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_PARASITIC_COLLECTIONS = EL_DELETE_LOCATIONS; //Same rights, re-use
    public static final String EL_DELETE_TREATMENTS = EL_DELETE_LOCATIONS; //Same rights, re-use

    public static String TH_EL(String elConstantName) throws IllegalAccessException
    {
        String thEl = "false";
        List<Field> fieldList = Arrays.stream(SecurityHelper.class.getDeclaredFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> Modifier.isFinal(f.getModifiers()))
                .filter(f -> f.getType().equals(String.class))
                .filter(f -> f.getName().equals(elConstantName))
                .collect(Collectors.toList());
        if (fieldList.size() > 0) {
            thEl = "#authorization.expression('";
            thEl += ((String)fieldList.get(0).get(null)).replaceAll("'", "''");
            thEl += "')";
        }
        return thEl;
    }
}
