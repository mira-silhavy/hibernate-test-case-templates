package org.hibernate.bugs;

import jakarta.persistence.*;

@Entity
@Table(name = "SITE_USER")
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private CommunityProfile communityProfile;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "WALLET_ID", nullable = false)
    private Wallet wallet;

    public SiteUser() {
    }

    public Profile getCommunityProfile() {
        return communityProfile;
    }

    public void setCommunityProfile(CommunityProfile communityProfile) {
        this.communityProfile = communityProfile;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
