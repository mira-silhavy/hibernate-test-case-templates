package org.hibernate.bugs;

import jakarta.persistence.*;

@Entity
@Table(name = "WALLET")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "wallet", optional = false)
    private SiteUser user;

    @Column(name = "WALLET_NAME")
    private String walletName;

    public Wallet() {
    }

    public Wallet(SiteUser user, String walletName) {
        this.user = user;
        this.walletName = walletName;
    }
}
