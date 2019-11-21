package ca.gc.dfo.slims.domain.entity.common.location;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ZHUY
 *
 */
@Getter
@Setter
@Embeddable
public class GeoUTM
{
	
	@Column(name = "FKREF_MAP_DATUM_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	mapDatum;
	
	@Column(name = "FKREF_UTM_ZONE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	utmZone;
	
	@Column(name = "FKREF_UTM_BAND_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	utmBAND;
	
	@Column(name = "FKREF_UTM_ZONE_BAND_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode	utmZoneBand;
	
	@Column(name = "UTM_EASTING_1", columnDefinition = "NUMBER(9,2)")
	private Double	utmEasting01;
	
	@Column(name = "UTM_NORTHING_1", columnDefinition = "NUMBER(9,2)")
	private Double	utmNorthing01;
	
	@Column(name = "UTM_EASTING_2", columnDefinition = "NUMBER(9,2)")
	private Double	utmEasting02;
	
	@Column(name = "UTM_NORTHING_2", columnDefinition = "NUMBER(9,2)")
	private Double	utmNorthing02;
	
	@Column(name = "UTM_EASTING_3", columnDefinition = "NUMBER(9,2)")
	private Double	utmEasting03;
	
	@Column(name = "UTM_NORTHING_3", columnDefinition = "NUMBER(9,2)")
	private Double	utmNorthing03;
	
	@Column(name = "UTM_EASTING_4", columnDefinition = "NUMBER(9,2)")
	private Double	utmEasting04;
	
	@Column(name = "UTM_NORTHING_4", columnDefinition = "NUMBER(9,2)")
	private Double	utmNorthing04;
	
	private String	location;
	
}
