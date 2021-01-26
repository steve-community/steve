package net.parkl.ocpp.service.cs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.parkl.ocpp.repositories.OcppAddressRepository;
import net.parkl.ocpp.repositories.OcppTagRepository;
import net.parkl.ocpp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.repository.dto.User.Details;
import de.rwth.idsg.steve.repository.dto.User.Overview;
import de.rwth.idsg.steve.web.dto.UserForm;
import de.rwth.idsg.steve.web.dto.UserQueryForm;
import net.parkl.ocpp.entities.OcppAddress;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.entities.User;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private OcppTagRepository tagRepo;
	@Autowired
	private AddressService addressService;
	@Autowired
	private OcppAddressRepository addressRepo;
	
	@PersistenceContext
	private EntityManager em;
	

	@Override
	@Transactional
	public void update(UserForm form) {
		User user = userRepo.findById(form.getUserPk()).
				orElseThrow(() -> new IllegalArgumentException("Invalid user id: "+form.getUserPk()));
		
		
		OcppTag tag = tagRepo.findByIdTag(form.getOcppIdTag());
		if (tag==null) {
			throw new IllegalArgumentException("Invalid id tag: "+form.getOcppIdTag());
		}
		OcppAddress addr = addressService.saveAddress(form.getAddress());
		user.setAddress(addr);
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		if (form.getBirthDay()!=null) {
			user.setBirthDay(form.getBirthDay().toDate());
		}
		if (form.getSex()!=null) {
			user.setSex(form.getSex().getDatabaseValue());
		}
		user.setPhone(form.getPhone());
		user.setEmail(form.getEMail());
		user.setNote(form.getNote());
		user.setOcppTag(tag);
		userRepo.save(user);
	}

	@Override
	@Transactional
	public void delete(int userPk) {
		User user = userRepo.findById(userPk).orElseThrow(() -> new IllegalArgumentException("Invalid user id: "+userPk));
		
		if (user.getAddress()!=null) {
			addressRepo.delete(user.getAddress());
		}
		userRepo.delete(user);
		
	}

	@Override
	@Transactional
	public void add(UserForm form) {
		OcppTag tag = tagRepo.findByIdTag(form.getOcppIdTag());
		
		OcppAddress addr = addressService.saveAddress(form.getAddress());
		User user=new User();
		user.setAddress(addr);
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		if (form.getBirthDay()!=null) {
			user.setBirthDay(form.getBirthDay().toDate());
		}
		if (form.getSex()!=null) {
			user.setSex(form.getSex().getDatabaseValue());
		}
		user.setPhone(form.getPhone());
		user.setEmail(form.getEMail());
		user.setNote(form.getNote());
		user.setOcppTag(tag);
		userRepo.save(user);
	}

	@Override
	public List<Overview> getOverview(UserQueryForm form) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> root = cq.from(User.class);
			cq.select(root);
			
	        if (form.isSetUserPk()) {
	        	cq=cq.where(cb.equal(root.get("userPk"), form.getUserPk()));
	        }

	        if (form.isSetOcppIdTag()) {
	        	cq=cq.where(cb.like(root.get("ocppTag").get("idTag"), "%"+form.getOcppIdTag()+"%"));
	            //selectQuery.addConditions(includes(OCPP_TAG.ID_TAG, form.getOcppIdTag()));
	        }

	        if (form.isSetEmail()) {
	        	cq=cq.where(cb.like(root.get("email"), "%"+form.getEmail()+"%"));
	            //selectQuery.addConditions(includes(USER.E_MAIL, form.getEmail()));
	        }

	        if (form.isSetName()) {
	        	cq=cq.where(cb.or(cb.like(root.get("firstName"), "%"+form.getName()+"%"),
	        			cb.like(root.get("lastName"), "%"+form.getName()+"%")));
	            // Concatenate the two columns and search within the resulting representation
	            // for flexibility, since the user can search by first or last name, or both.
	            //Field<String> joinedField = DSL.concat(USER.FIRST_NAME, USER.LAST_NAME);

	            // Find a matching sequence anywhere within the concatenated representation
	            //selectQuery.addConditions(includes(joinedField, form.getName()));
	        }

	        
	       	
			cq=cq.orderBy(cb.asc(root.get("userPk")));
			TypedQuery<User> q = em.createQuery(cq);
			List<User> result = q.getResultList();
			

			List<Overview> ret=new ArrayList<>();
			for (User u:result) {
				
				ret.add(de.rwth.idsg.steve.repository.dto.User.Overview.builder()
                        .userPk(u.getUserPk())
                        .ocppTagPk(u.getOcppTag().getOcppTagPk())
                        .ocppIdTag(u.getOcppTag().getIdTag())
                        .name(u.getFirstName() + " " + u.getLastName())
                        .phone(u.getPhone())
                        .email(u.getEmail())
                        .build());
			}
			return ret;
		} finally { 
			em.close();
		}
	}

	@Override
	@Transactional
	public Details getDetails(int userPk) {
		// -------------------------------------------------------------------------
        // 1. user table
        // -------------------------------------------------------------------------

        User ur = userRepo.findById(userPk).orElseThrow(() -> new SteveException("There is no user with id '%s'", userPk));


        // -------------------------------------------------------------------------
        // 2. address table
        // -------------------------------------------------------------------------

        OcppAddress ar = ur.getAddress();

        // -------------------------------------------------------------------------
        // 3. ocpp_tag table
        // -------------------------------------------------------------------------

        String ocppIdTag = null;
        if (ur.getOcppTag() != null) {
            
            ocppIdTag = ur.getOcppTag().getIdTag();
            
        }

        return de.rwth.idsg.steve.repository.dto.User.Details.builder()
                           .userRecord(ur)
                           .address(ar)
                           .ocppIdTag(Optional.ofNullable(ocppIdTag))
                           .build();
	}

}
