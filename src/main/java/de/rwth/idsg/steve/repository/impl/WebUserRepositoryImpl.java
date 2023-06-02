/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.repository.impl;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.WebUserRepository;
import de.rwth.idsg.steve.repository.dto.WebUser;
import de.rwth.idsg.steve.web.dto.WebUserForm;
import de.rwth.idsg.steve.web.dto.WebUserQueryForm;
import jooq.steve.db.tables.records.WebauthoritiesRecord;
import jooq.steve.db.tables.records.WebusersRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
//import org.jooq.Field;
import org.jooq.JoinType;
//import org.jooq.Record1;
//import org.jooq.Record2;
import org.jooq.Record3;
//import org.jooq.Record7;
import org.jooq.Result;
//import org.jooq.SelectConditionStep;
import org.jooq.SelectQuery;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
//import java.util.Optional;

import static de.rwth.idsg.steve.utils.CustomDSL.includes;
import static jooq.steve.db.tables.Webusers.WEBUSERS;
import static jooq.steve.db.tables.Webauthorities.WEBAUTHORITIES;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


 /**
 * @author Frank Brosi
 * @since 01.04.2022
 */
@Slf4j
@Repository
public class WebUserRepositoryImpl implements WebUserRepository {

    @Autowired private DSLContext ctx;
    //@Autowired private AddressRepository addressRepository;
    
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public List<WebUser.Overview> getOverview(WebUserQueryForm form) {
        return getOverviewInternal(form)
                .map(r -> WebUser.Overview.builder()
                                       .webusername(r.value1())
                                       .enabled(r.value2())
                                       .roles(r.value3())
                                       .build()
                );
    }
    
   
    @Override
    public WebUser.Details getDetails(String webusername) {

        // -------------------------------------------------------------------------
        // 1. user table
        // -------------------------------------------------------------------------

        WebusersRecord ur = ctx.selectFrom(WEBUSERS)
                           .where(WEBUSERS.USERNAME.equal(webusername))
                           .fetchOne();

        if (ur == null) {
            throw new SteveException("There is no user with id '%s'", webusername);
        }

        // -------------------------------------------------------------------------
        // 2. address table
        // -------------------------------------------------------------------------

        List<WebauthoritiesRecord> Lar = ctx.selectFrom(WEBAUTHORITIES)
                .where(WEBAUTHORITIES.USERNAME.eq(webusername))
                .fetch();

        if (Lar == null) {
            throw new SteveException("There is no role for user '%s' defined", webusername);
        }
        
        // -------------------------------------------------------------------------
        // 3. ocpp_tag table
        // -------------------------------------------------------------------------

        
        return WebUser.Details.builder()
                           .webusersRecord(ur)
                           .webauthoritiesRecord_List(Lar)
                           .build();
    }

    @Override
    public void add(WebUserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                addInternal(ctx, form);
            } catch (DataAccessException e) {
                throw new SteveException("Failed to add the user", e);
            }
        });
    }

    @Override
    public void update(WebUserForm form) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                updateInternal(ctx, form);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to update the webuser", e);
            }
        });
    }

    @Override
    public void delete(String webusername, String role) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                deleteInternal(ctx, webusername, role);

            } catch (DataAccessException e) {
                throw new SteveException("Failed to delete the webuser", e);
            }
        });
    }
    
    @Override
    public void delete(String webusername) {
        ctx.transaction(configuration -> {
            DSLContext ctx = DSL.using(configuration);
            try {
                deleteInternal(ctx, webusername);
            } catch (DataAccessException e) {
                throw new SteveException("Failed to delete the webuser", e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    private Result<Record3<String, Boolean, String>> getOverviewInternal(WebUserQueryForm form) {
        SelectQuery selectQuery = ctx.selectQuery();
        selectQuery.addFrom(WEBUSERS);
        selectQuery.addJoin(WEBAUTHORITIES, JoinType.LEFT_OUTER_JOIN, WEBUSERS.USERNAME.eq(WEBAUTHORITIES.USERNAME));
        selectQuery.addSelect(
                WEBUSERS.USERNAME,
                WEBUSERS.ENABLED,
                WEBAUTHORITIES.AUTHORITY
        );

        if (form.isSetWebusername()) {
            selectQuery.addConditions(WEBUSERS.USERNAME.eq(form.getWebusername()));
        }

        if (form.isSetEnabled()) {
            selectQuery.addConditions(WEBUSERS.ENABLED.eq(form.getEnabled()));
        }

        if (form.isSetRoles()) {
            String[] roles = form.getRoles().split(";"); //Semicolon seperated String to StringArray
            for (String role : roles)
            {
                selectQuery.addConditions(includes(WEBAUTHORITIES.AUTHORITY, role.strip())); // strip--> No Withspace
            }
        }

        return selectQuery.fetch();
    }
    


    private void addInternal(DSLContext ctx, WebUserForm form) {
        int count = 0;
        
        count = ctx.insertInto(WEBUSERS)
                       .set(WEBUSERS.USERNAME, form.getWebusername())
                       .set(WEBUSERS.ENABLED, form.getEnabled())
                       .set(WEBUSERS.PASSWORD, encoder.encode(form.getPassword()))
                       .execute();
        
        String[] roles = form.getRoles().split(";"); //Semicolon seperated String to StringArray
        for (String role : roles)
        {
            count =+ ctx.insertInto(WEBAUTHORITIES)
                       .set(WEBAUTHORITIES.USERNAME, form.getWebusername())
                       .set(WEBAUTHORITIES.AUTHORITY, role.strip())
                       .execute();
        }

        if (count == 0) {
            throw new SteveException("Failed to insert the user");
        }
    }

    private void updateInternal(DSLContext ctx, WebUserForm form) {
        if (form.getPassword()==null)
        {
            ctx.update(WEBUSERS)
                .set(WEBUSERS.USERNAME, form.getWebusername())
                .set(WEBUSERS.ENABLED, form.getEnabled())
                //.set(WEBUSERS.PASSWORD, encoder.encode(form.getPassword()))
                .where(WEBUSERS.USERNAME.eq(form.getWebusername())) //set Username unnessary until WebUserForm has oldName or the table and the form uses a primary key
                .execute();
        }
        else
        {
            ctx.update(WEBUSERS)
                .set(WEBUSERS.USERNAME, form.getWebusername())
                .set(WEBUSERS.ENABLED, form.getEnabled())
                .set(WEBUSERS.PASSWORD, encoder.encode(form.getPassword()))
                .where(WEBUSERS.USERNAME.eq(form.getWebusername())) //set Username unnessary until WebUserForm has oldName or the table and the form uses a primary key
                .execute();
        }
        // delete all Authority entries for this webuser 
        ctx.delete(WEBAUTHORITIES)
           .where(WEBAUTHORITIES.USERNAME.equal(form.getWebusername()))
           .execute();
        
        String[] roles = form.getRoles().split(";"); //Semicolon seperated String to StringArray
        for (String role : roles)
        {
            ctx.insertInto(WEBAUTHORITIES)
                .set(WEBAUTHORITIES.USERNAME, form.getWebusername())
                .set(WEBAUTHORITIES.AUTHORITY, role.strip())
                .execute();
        }
    }

    private void deleteInternal(DSLContext ctx, String webusername, String role) {
        
        ctx.delete(WEBAUTHORITIES)
           .where(WEBAUTHORITIES.USERNAME.equal(webusername))
           .and(WEBAUTHORITIES.AUTHORITY.eq(role))
           .execute();
        
        boolean isEmpty = ctx.selectFrom(WEBAUTHORITIES)
                .where(WEBAUTHORITIES.USERNAME.equal(webusername))
                .fetch().isEmpty();
        
        if (isEmpty){
        ctx.delete(WEBUSERS)
           .where(WEBUSERS.USERNAME.equal(webusername))
           .execute();
        }
        
    }
    
    private void deleteInternal(DSLContext ctx, String webusername) {
    
        Integer count = ctx.selectCount().from(WEBUSERS).execute();
        
        if (count > 1) // don't delete the last webuser!
        {
            ctx.delete(WEBAUTHORITIES)
               .where(WEBAUTHORITIES.USERNAME.equal(webusername))
               .execute();

            ctx.delete(WEBUSERS)
               .where(WEBUSERS.USERNAME.equal(webusername))
               .execute();
        }
    }
}
