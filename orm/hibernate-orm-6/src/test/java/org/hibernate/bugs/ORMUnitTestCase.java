/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Proxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static jakarta.persistence.FetchType.LAZY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
 * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
 * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
 * simplifies the process.
 * <p>
 * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
 * submit it as a PR!
 */
public class ORMUnitTestCase extends BaseCoreFunctionalTestCase {

    interface Site {
        String getFieldName();
    }

    interface ASite extends Site {
        String getAField();

        default String getFieldName() {
            return getAField();
        }
    }

    interface BSite extends Site {
        String getBField();

        default String getFieldName() {
            return getBField();
        }
    }

    interface CSite extends Site {
        String getCField();

        default String getFieldName() {
            return getCField();
        }
    }

    interface DSite extends Site {
        String getDField();

        default String getFieldName() {
            return getDField();
        }
    }

    @Entity
    public static class User {

        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(targetEntity = SiteImpl.class, fetch = LAZY)
        @JoinColumn(name = "SITE_ID", nullable = false)
        private Site site;

        public Long getId() {
            return id;
        }

        public void setSite(Site site) {
            this.site = site;
        }

        public Site getSite() {
            return site;
        }
    }

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @Proxy(proxyClass = Site.class)
    public static abstract class SiteImpl implements Site {
        @Id
        @GeneratedValue
        private Long id;
    }

    @Entity
    @Proxy(proxyClass = ASite.class)
    public static class ASiteImpl extends SiteImpl implements ASite {
        private String aField = "a";

        @Override
        public String getAField() {
            return aField;
        }
    }

    @Entity
    @Proxy(proxyClass = BSite.class)
    public static class BSiteImpl extends SiteImpl implements BSite {
        private String bField = "b";

        @Override
        public String getBField() {
            return bField;
        }
    }

    @Entity
    @Proxy(proxyClass = CSite.class)
    public static class CSiteImpl extends SiteImpl implements CSite {
        private String bField = "c";

        @Override
        public String getCField() {
            return bField;
        }
    }

    @Entity
    @Proxy(proxyClass = DSite.class)
    public static class DSiteImpl extends SiteImpl implements DSite {
        private String dField = "d";

        @Override
        public String getDField() {
            return dField;
        }
    }

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                User.class,
                SiteImpl.class,
                ASiteImpl.class,
                BSiteImpl.class,
                CSiteImpl.class,
                DSiteImpl.class,
        };
    }

    // Add your tests, using standard JUnit.
    @Test
    public void testProxyWithInheritance() {
        // BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
        Session s = openSession();
        Transaction tx = s.beginTransaction();

        CSiteImpl cSite = new CSiteImpl();
        session.persist(cSite);

        User user = new User();
        user.setSite(cSite);
        session.persist(user);

        session.flush();
        session.clear();

        user = session.load(User.class, user.getId());
        assertFalse(Hibernate.isInitialized(user));
        assertEquals("c", user.getSite().getFieldName());

        tx.commit();
        s.close();
    }
}
