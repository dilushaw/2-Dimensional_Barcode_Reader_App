//package lk.dialog.corporate.Qr.dataaccess;
//
//import java.util.ArrayList;
//import lk.dialog.corporate.Qr.data.Privileges;
//import org.hibernate.HibernateException;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//
///**
// * @author Anuradha
// */
//public class ActionPrivileges_Dao {
//
//    Transaction tx = null;
//    Session session = null;
//
//    /*
//     * start tranaction
//     */
//    public void startTransaction() {
//
//
//        try {
//            if (session == null || !session.isOpen()) {
//                HibernateSession hib_sess = new HibernateSession();
//                session = hib_sess.getSession();
//            }
//            tx = session.beginTransaction();
//        } catch (HibernateException e) {
//            //logger.error(e.toString());
//
//        } catch (RuntimeException e) {
//            //logger.error(e.toString());
//        }
//
//    }
//    /*
//     * insert data for table
//     */
//
//    public void saveTableRow(Object o) {
//        try {
//            session.save(o);
//        } catch (HibernateException e) {
//            tx.rollback();
//            //logger.error(e.toString());
//        }
//    }
//    /*
//     * commit tranaction
//     */
//
//    public void commitTransaction() {
//        try {
//            tx.commit();
//        } catch (HibernateException e) {
//            // logger.error(e.toString());
//        }
//
//    }
//
//    public void updateTableRow(Object o) {
//        try {
//
//            session.update(o);
//
//        } catch (HibernateException e) {
//            tx.rollback();
//            // logger.error(e.toString());
//        }
//    }
//
//    public String getRoleId(String userName) {
//        String role_id = null;
//        startTransaction();
//
//        Query q1 = session.createQuery("select  user.role.roleId from User user where user.userName='" + userName + "'");
//
//        ArrayList<String> role_id_list = (ArrayList<String>) q1.list();
//        role_id = role_id_list.get(0);
//
//        commitTransaction();
//        return role_id;
//    }
//
//    public String getRoleType(String roleid) {
//        String role_type = null;
//        startTransaction();
//
//        Query q1 = session.createQuery("select  role.roleType from Role role where role.roleId='" + roleid + "'");
//
//        ArrayList<String> role_type_list = (ArrayList<String>) q1.list();
//        role_type = role_type_list.get(0);
//
//        commitTransaction();
//        return role_type;
//    }
//
//    public ArrayList<String> getPrivilegeIds(String roletype) {
//
//        ArrayList<String> privilege_id_list = null;
//
//        startTransaction();
//
//        Query q1 = session.createQuery("select  roleprivilege.privileges.privilegeId from RolePrivilege roleprivilege where roleprivilege.role.roleType='" + roletype + "'");
//
//        privilege_id_list = (ArrayList<String>) q1.list();
//
//        commitTransaction();
//        return privilege_id_list;
//    }
//
//    public ArrayList<String> getPrivilegeNameList(String userName) {
//
//        String role_id = getRoleId(userName);
//        String role_type = getRoleType(role_id);
//        ArrayList<String> privilege_id = getPrivilegeIds(role_type);
//
//
//        ArrayList<String> privilege_name_list = null;
//
//        startTransaction();
//
//        for (int i = 0; i < privilege_id.size(); i++) {
//
//            Privileges privileges_obj = (Privileges) session.get(Privileges.class, privilege_id.get(i));
//
//            String priveName = privileges_obj.getPrivilegeName();
//
//            if (privilege_name_list == null) {
//                privilege_name_list = new ArrayList<String>();
//            }
//            privilege_name_list.add(priveName);
//
//        }
//        commitTransaction();
//        return privilege_name_list;
//    }
//}
