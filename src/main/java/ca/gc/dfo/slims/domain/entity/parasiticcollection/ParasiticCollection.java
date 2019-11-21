package ca.gc.dfo.slims.domain.entity.parasiticcollection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.converter.RefCodeLongConverter;
import ca.gc.dfo.slims.domain.entity.common.LakeDistrict;
import ca.gc.dfo.slims.domain.entity.common.RefCode;
import ca.gc.dfo.slims.domain.entity.common.Sample;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_parasitic_collections")
public class ParasiticCollection
{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paracollection_sequence")
	@SequenceGenerator(name = "paracollection_sequence", initialValue = 1, allocationSize = 1, sequenceName = "PARACOLLECTION_SEQ")
	private Long						id;
	
	@Embedded
	private Sample						sample;
	
	@Column(name = "COLLECTED_DATE")
	@Temporal(TemporalType.DATE)
	private Date						collectedDate;
	
	private String						fisherName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lakeDistrict_id", nullable = false)
	@Fetch(FetchMode.JOIN)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private LakeDistrict				lakeDistrict;
	
	@Column(name = "FKREF_MANAGEMENT_UNIT_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode						managementUnit;
	
	@Column(name = "GRID_NUMBER", columnDefinition = "NUMBER(9,2)")
	private Double						gridNumber;
	
	@Column(name = "GRID_NUMBER_EST", columnDefinition = "NUMBER(9,2)")
	private Double						gridNumberEst;
	
	@Column(name = "FKREF_GEAR_TYPE_ID")
	@Convert(converter = RefCodeLongConverter.class)
	private RefCode						gearType;
	
	@Column(name = "MESH_SIZE", columnDefinition = "NUMBER(9,2)")
	private Double						meshSize;
	
	@Column(name = "MESH_SIZE_MAX", columnDefinition = "NUMBER(9,2)")
	private Double						meshSizeMax;
	
	@Column(name = "MESH_SIZE_MIN", columnDefinition = "NUMBER(9,2)")
	private Double						meshSizeMin;
	
	@Column(name = "MESH_SIZE_AVG", columnDefinition = "NUMBER(9,2)")
	private Double						meshSizeAvg;
	
	@Column(name = "WATER_DEPTH", columnDefinition = "NUMBER(9,2)")
	private Double						waterDepth;
	
	@Column(name = "DEPTH_MAX", columnDefinition = "NUMBER(9,2)")
	private Double						depthMax;
		
	@Column(name = "DEPTH_MIN", columnDefinition = "NUMBER(9,2)")
	private Double						depthMin;
		
	@Column(name = "DEPTH_AVG", columnDefinition = "NUMBER(9,2)")
	private Double						depthAvg;
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parasiticCollection", orphanRemoval = true)
	@JsonManagedReference
	private List<ParasiticAttachment>	parasiticAttachments	= new ArrayList<ParasiticAttachment>();
	
	public void setParasiticAttachments(List<ParasiticAttachment> parasiticAttachments)
	{
		this.parasiticAttachments.clear();
		this.parasiticAttachments.addAll(parasiticAttachments);
	}
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date	dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date	dateModified;
	
}
