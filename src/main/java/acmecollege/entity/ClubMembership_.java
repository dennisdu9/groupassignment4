package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2023-04-04T17:25:42.541-0400")
@StaticMetamodel(ClubMembership.class)
public class ClubMembership_ extends PojoBase_ {
	public static volatile SingularAttribute<ClubMembership, StudentClub> club;
	public static volatile SingularAttribute<ClubMembership, MembershipCard> card;
	public static volatile SingularAttribute<ClubMembership, DurationAndStatus> durationAndStatus;
}
