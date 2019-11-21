package ca.gc.dfo.slims.utility;

import ca.gc.dfo.slims.constants.ResponseMessages;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import org.springframework.http.HttpStatus;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class SampleUtils {
    private SampleUtils() {
        throw new UnsupportedOperationException("Should not call this constructor");
    }

    public static Sample getSampleFromFormData(Map<String, String> formData,
                                               Lake lake,
                                               Stream stream,
                                               Long nextSeqNumber,
                                               String sampleCodePrefix,
                                               AppMessages messages) throws ParseException {
        String methodName = "SampleUtils:getSampleFromFromData";
        String lakeCode = lake == null ?  null : lake.getLakeCode();
        String streamCode = stream == null ? null : stream.getStreamCode();
        CommonUtils.getLogger().debug("{} for ({}) with nextSeqNumber({}), lake({}, stream({}) and formData {}",
            methodName, sampleCodePrefix, nextSeqNumber, lakeCode, streamCode, formData.toString());

        String yearStr = Year.now().format(DateTimeFormatter.ofPattern("uu"));

        String counterStr = getSampleIdEndPortion(nextSeqNumber);
        if (counterStr == null) {
            CommonUtils.getLogger().error("{} failed to get {} counter for lake({}) stream({}) with nextSeqNumber({})",
                methodName, sampleCodePrefix, lakeCode, streamCode, nextSeqNumber);
            ExceptionUtils.throwResponseException(
                HttpStatus.BAD_REQUEST, messages.get(ResponseMessages.BAD_SAMPLEID_COUNTERFORMAT.getName()));
        }

        String sampleCode = (sampleCodePrefix + lakeCode + streamCode + yearStr + counterStr).toUpperCase();

        Sample sample = new Sample();
        sample.setSampleDate(CommonUtils.getDateValue(formData.get("sampleDate")));
        sample.setSampleStatus("DRAFT");
        sample.setSampleCode(sampleCode);

        CommonUtils.getLogger().debug("{} return Sample object for ({})", methodName, sampleCode);
        return sample;
    }

    private static String getSampleIdEndPortion(long counter) {
        // the endportion of a sample id is like: nnnn, which is the sequential number in DB of an entity.
        // counter is the total count+1 of the entity in DB
        // precision is a float number to help get the decimal portion to create 0.nnnn.
        // For example if the endportion is expected to be as nnnn, then precision should be 0.0001
        double precision = 0.0001;
        double portionNum = ((double) counter) * precision;
        if (portionNum < 1) {
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(Double.toString(precision).length() - 2);

            String returnStr = df.format(portionNum);
            return returnStr.substring(returnStr.indexOf(".") + 1);
        }
        return Long.toString(counter);
    }
}
