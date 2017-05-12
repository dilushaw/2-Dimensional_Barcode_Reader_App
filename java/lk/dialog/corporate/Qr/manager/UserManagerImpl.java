/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.*;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dataaccess.*;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class UserManagerImpl implements UserManager {

//    private static UserManager userManager=null;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private User user;
    @Autowired
    private PrivilegesDAO privilegesDAO;
    @Autowired
    private CampaignDAO campaignDAO;
    @Autowired
    private UserPrivilegesDAO userPrivilegesDAO;
    @Autowired
    private UserCampaignDAO userCampaignDAO;

//    public static UserManager getInstance(){
//        if(userManager == null){
//            userManager = new UserManagerImpl();
//        }
//        
//        return userManager;
//    }
//    private UserManagerImpl(){
//        init();
//    }
//    
//    public void init(){
//       userDAO = new UserDAOImpl(); 
//    }
    public List<UserPrivileges> loadUserPrivileges(Number id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void assignPriviledgesToUser(Number userId, List<Privileges> privileges) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public User loadLoginUser(User user) throws QrException {

        User loginUser = null;
        try {
            HibernateUtil.beginTransaction();
            loginUser = userDAO.findLoginUser(user.getUserName(), user.getPassword(), user.getCorporate().getCorporateAccount());
            HibernateUtil.commitTransaction();

        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return loginUser;

    }

    public List<UserPrivileges> loadDefaultPrivileges(String roleType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public User findUserByID(Number id) throws QrException {
        User user = null;
        try {
            HibernateUtil.beginTransaction();
            user = userDAO.findByID(User.class, id);
            HibernateUtil.commitTransaction();

        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return user;
    }

    public User findUserByUserNameAndAccount(String userName, String account) throws QrException {
        User user = null;
        try {
            HibernateUtil.beginTransaction();
            user = userDAO.findUserByUserNameAndAccount(userName, account);
            HibernateUtil.commitTransaction();

        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return user;
    }

    public void prepareAndSetPrivilages(Integer[] privileges, User user) throws QrException {
        try {
            Set<UserPrivileges> userPrivis = new HashSet<UserPrivileges>();
            for (int i = 0; i < privileges.length; i++) {
                UserPrivileges userPrivi = new UserPrivileges();
                userPrivi.setPrivileges(findPriviledgeById(privileges[i]));
                userPrivi.setUser(user);
                HibernateUtil.beginTransaction();
                userPrivilegesDAO.save(userPrivi);
                HibernateUtil.commitTransaction();
                userPrivis.add(userPrivi);

            }
            user.setUserPrivilegeses(userPrivis);
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public Privileges findPriviledgeById(Integer id) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            Privileges privis = privilegesDAO.findByID(Privileges.class, id);
            HibernateUtil.commitTransaction();
            return privis;
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void prepareAndSetCampaigns(Integer[] campaigns, User user) throws QrException {
        try {
            Set<UserCampaign> userCampaigns = new HashSet<UserCampaign>();
            for (int i = 0; i < campaigns.length; i++) {
                UserCampaign userCamp = new UserCampaign();
                userCamp.setCampaign(findCampaignById(campaigns[i]));
                userCamp.setUser(user);
                HibernateUtil.beginTransaction();
                userCampaignDAO.save(userCamp);
                HibernateUtil.commitTransaction();
                userCampaigns.add(userCamp);

            }
            user.setUserCampaigns(userCampaigns);
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public Campaign findCampaignById(Integer id) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            Campaign cam = campaignDAO.findByID(Campaign.class, id);
            HibernateUtil.commitTransaction();
            return cam;
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public Map<String, String> prparePriviForSecurity(Set<UserPrivileges> userPrivilegeses) {
        HashMap<String, String> priviMap = null;
        if (userPrivilegeses != null) {
            priviMap = new HashMap<String, String>();
            for (UserPrivileges up : userPrivilegeses) {

                priviMap.put(up.getPrivileges().getKey(), up.getPrivileges().getPrivilegeName());
            }
        }
        return priviMap;
    }

    public void updateUser(User user) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            userDAO.update(user);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void setCUCreatedCampaignForCU(Campaign campaign, User user) throws QrException {
        try {
            Set<UserCampaign> userCampaigns = new HashSet<UserCampaign>();

            UserCampaign userCamp = new UserCampaign();
            userCamp.setCampaign(campaign);
            userCamp.setUser(user);
            HibernateUtil.beginTransaction();
            userCampaignDAO.save(userCamp);
            HibernateUtil.commitTransaction();
            userCampaigns.add(userCamp);
            user.setUserCampaigns(userCampaigns);
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }
}
