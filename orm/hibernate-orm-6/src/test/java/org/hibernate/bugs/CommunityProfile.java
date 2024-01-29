package org.hibernate.bugs;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("COMMUNITY")
public class CommunityProfile extends Profile {

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "communityProfile", optional = false)
    private SiteUser user;

    @Column(name = "PROFILE_NAME")
    private String profileName;

    public CommunityProfile() {
    }

    public CommunityProfile(SiteUser user, String profileName) {
        this.user = user;
        this.profileName = profileName;
    }

    public SiteUser getUser() {
        return user;
    }

    public void setProfileName(String newProfile) {
        this.profileName = newProfile;
    }
}
