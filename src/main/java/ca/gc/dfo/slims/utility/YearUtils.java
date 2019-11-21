package ca.gc.dfo.slims.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class YearUtils {
    public enum OPER_YEAR {
        ALL,
        EQUAL,
        GTE,
        LTE
    }

    private YearUtils() {
        throw new UnsupportedOperationException("Should not call this constructor");
    }

    public static OPER_YEAR getYearOperation(String yearOp, String year) {
        if (!year.equalsIgnoreCase("All")) {
            if (yearOp.equalsIgnoreCase("equal")) {
                return OPER_YEAR.EQUAL;
            }
            if (yearOp.equalsIgnoreCase("gte")) {
                return OPER_YEAR.GTE;
            }
            if (yearOp.equalsIgnoreCase("lte")) {
                return OPER_YEAR.LTE;
            }
        }
        return OPER_YEAR.ALL;
    }

    public static List<Integer> getYearList(Date theDate, String callerName) {
        List<Integer> yearList = new ArrayList<>();
        int currYear = LocalDate.now().getYear();
        yearList.add(currYear);

        if (theDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(theDate);
            int year = calendar.get(Calendar.YEAR);
            for (int i = currYear - 1; i > year; i--) {
                yearList.add(i);
            }
        }
        CommonUtils.getLogger().debug("{}:getYearList got {} years in yearList", callerName, yearList.size());
        return yearList;
    }
}
