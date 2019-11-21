package ca.gc.dfo.slims.domain.entity.treatments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import ca.gc.dfo.slims.domain.entity.common.Sample;
import ca.gc.dfo.slims.domain.entity.common.location.Lake;
import ca.gc.dfo.slims.domain.entity.common.location.Stream;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value =
{ "dateCreated", "dateModified" }, allowGetters = true)
@Entity
@Table(name = "slims_treatments")
public class Treatment
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "treatments_sequence")
	@SequenceGenerator(name = "treatments_sequence", initialValue = 1, allocationSize = 1, sequenceName = "TREATMENT_SEQ")
	private Long						id;
	
	@Embedded
	private Sample						sample;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lake_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Lake						lake;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stream_id", nullable = false)
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private Stream						stream;
	
	@OneToOne(mappedBy = "treatment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval = true)
	@JsonManagedReference
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	private TRLogistics					trLogistics;
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRPrimaryApplication>	trPrimaryApplications	= new ArrayList<TRPrimaryApplication>();
	
	public void setTrPrimaryApplications(List<TRPrimaryApplication> trPrimaryApplications)
	{
		this.trPrimaryApplications.clear();
		this.trPrimaryApplications.addAll(trPrimaryApplications);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRSecondaryApplication> trSecondaryApplications = new ArrayList<TRSecondaryApplication>();
	
	public void setTrSecondaryApplications(List<TRSecondaryApplication> trSecondaryApplications)
	{
		this.trSecondaryApplications.clear();
		this.trSecondaryApplications.addAll(trSecondaryApplications);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRDesiredConcentration> desiredConcentrations = new ArrayList<TRDesiredConcentration>();
	
	public void setDesiredConcentrations(List<TRDesiredConcentration> desiredConcentrations)
	{
		this.desiredConcentrations.clear();
		this.desiredConcentrations.addAll(desiredConcentrations);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRMinLethalConcentration> trMinLethalConcentrations = new ArrayList<TRMinLethalConcentration>();
	
	public void setTrMinLethalConcentrations(List<TRMinLethalConcentration> trMinLethalConcentrations)
	{
		this.trMinLethalConcentrations.clear();
		this.trMinLethalConcentrations.addAll(trMinLethalConcentrations);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRWaterChemistry> trWaterChemistries = new ArrayList<TRWaterChemistry>();
	
	public void setTrWaterChemistries(List<TRWaterChemistry> trWaterChemistries)
	{
		this.trWaterChemistries.clear();
		this.trWaterChemistries.addAll(trWaterChemistries);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRDischarge> trDischarges = new ArrayList<TRDischarge>();
	
	public void setTrDischarges(List<TRDischarge> trDischarges)
	{
		this.trDischarges.clear();
		this.trDischarges.addAll(trDischarges);
	}
	
	@JsonIgnoreProperties(
	{ "hibernateLazyInitializer", "handler" })
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "treatment", orphanRemoval = true)
	@JsonManagedReference
	private List<TRChemicalAnalysis> trChemicalAnalysises = new ArrayList<TRChemicalAnalysis>();
	
	public void setTrChemicalAnalysises(List<TRChemicalAnalysis> trChemicalAnalysises)
	{
		this.trChemicalAnalysises.clear();
		this.trChemicalAnalysises.addAll(trChemicalAnalysises);
	}
	
	@Column(name = "DATE_CREATED")
	@Temporal(TemporalType.DATE)
	@CreatedDate
	private Date			dateCreated;
	
	@Column(name = "DATE_MODIFIED")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	private Date			dateModified;
	
	@Transient
	private double			totalTFMLiterUsed	= 0.0;
	
	@Transient
	private double			totalTFMBars		= 0.0;
	
	@Transient
	private double			totalBayluscideEC	= 0.0;
	
	@Transient
	private double			totalBayluscideWP	= 0.0;
	
	@Transient
	private double			totalBayluscideGB	= 0.0;
	
	@Transient
	private int				totalAdultPM		= 0;
	
	@Transient
	private List<String>	totalSpecies		= new ArrayList<String>();
	
	@PostLoad
	private void postLoad()
	{
		for (TRPrimaryApplication primApp : trPrimaryApplications)
		{
			this.totalTFMLiterUsed += getAllLitersUsed(primApp);
			this.totalBayluscideEC += getAllPAI(primApp);
			this.totalBayluscideWP += getAllPPB(primApp);
		}
		for (TRSecondaryApplication secondApp : trSecondaryApplications)
		{
			if (secondApp.getTrTFM() != null && secondApp.getTrTFM().getNumOfBars() != null)
				this.totalTFMBars += secondApp.getTrTFM().getNumOfBars();
			if (secondApp.getTrGranularBayluscide() != null && secondApp.getTrGranularBayluscide().getAmountUsed() != null)
				this.totalBayluscideGB += secondApp.getTrGranularBayluscide().getAmountUsed();
			if (secondApp.getTrSecondAppInducedMortality() != null && secondApp.getTrSecondAppInducedMortality().getAdults() != null)
			{
				//this.totalAdultPM += secondApp.getTrSecondAppInducedMortality().getAdults();
			}
		}
	}
	
	protected double getAllLitersUsed(TRPrimaryApplication primApp)
	{
		double returnVal = 0.0;
		for (TRTFM tfm : primApp.getTrTFMs())
		{
			if (tfm.getLitresUsed() != null)
			{
				returnVal += tfm.getLitresUsed();
			}
		}
		return returnVal;
	}
	
	protected double getAllPAI(TRPrimaryApplication primApp)
	{
		double returnVal = 0.0;
		for (TREmulsifiableConcentrate ec : primApp.getTrEmulsifiableConcentrates())
		{
			if (ec.getEcPercAI() != null)
			{
				String percAIVal = ec.getEcPercAI().getCodePair().getValueEn().split("%")[0];
				returnVal += Double.valueOf(percAIVal);
			}
		}
		return returnVal;
	}
	
	protected double getAllPPB(TRPrimaryApplication primApp)
	{
		double returnVal = 0.0;
		for (TRWettablePowder wp : primApp.getTrWettablePowders())
		{
			if (wp.getWpPercAI() != null)
			{
				String percAIVal = wp.getWpPercAI().getCodePair().getValueEn().split("%")[0];
				returnVal += Double.valueOf(percAIVal);
			}
		}
		return returnVal;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Treatment other = (Treatment) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
