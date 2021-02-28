package de.luzifer.core.api.manager;

import de.luzifer.core.api.check.Check;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    private static final List<Check> checks = new ArrayList<>();

    public static void registerCheck(Check check) {
        checks.add(check);
    }

    public static void deregisterCheck(Check check) {
        checks.remove(check);
    }

    public static List<Check> getChecks() {
        return checks;
    }

    public static boolean isRegisteredCheck(Check check) {
        return checks.contains(check);
    }
}
