package com.aja.ott.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrialUserFeedBack {

	// Resource Information
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private String contactNo;
	private String technology;

	@ManyToOne
	@JoinColumn(name = "trial_user_id")
	private TrialUser trialUser;

//FeedBack Questions
	private String overallExperience;
//(Options)--Excellent, Good, Average, Poor

	private String contentQuality;
//(Options)--Very Effective, Effective, Neutral, Ineffective

	private String codingTutorSupport;
//(Options)--Excellent, Good, Average, Poor
	@Column(nullable = false)
	private boolean isDeleted = false;

	private String engagementAndInteractivity;
//(Options)--Very Engaging, Engaging, Neutral, Not Engaging

	private String howMuchConfidenceYouGained;
//2 Weeks trail 
//(Options)--Confidence gained, No Confidence gained, Significant Confidence gained

	private String anySpecificExperienceOrAspectsOfProgram;
//Comment
//Share any specific experiences or aspects of the program 
//that contributed to your increases confidence 

	private String peerInteraction;
//Beneficial was the interaction with peers during the training
//(Options)--Very Beneficial, Somewhat Beneficial, Neutral, Not Beneficial 

	private String learningExperienceMeetExpectations;
//(Options)--Yes, No, Some how

	private String specificAspectsoflearning;

	private String specificAspectsoflearningReachYourExpectation;
//comment
//What specific aspects of the learning did or did not align with your expectations

	private String concernsIssueWithAja;

	private String decisionOfJoiningAja;

//(Options)--Yes I planned to join, No i have decided not to join, I'm still considering it

	private String additionalComment;

}