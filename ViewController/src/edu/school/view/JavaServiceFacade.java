package edu.school.view;

import edu.school.jpa.AbstractJpaEntity;
import edu.school.jpa.Availability;
import edu.school.jpa.UserIdJpaEntity;
import edu.school.jpa.ClassGroup;
import edu.school.jpa.ClassGroupSubjectMap;
import edu.school.jpa.ClassGroupSubjectMapPK;
import edu.school.jpa.ClassSubjectCount;
import edu.school.jpa.ClassSubjectCountPK;
import edu.school.jpa.Classroom;
import edu.school.jpa.Lesson;
import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import edu.school.jpa.TeacherSubjectMap;
import edu.school.jpa.TeacherSubjectMapPK;
import edu.school.jpa.TimeScheduleMaster;
import edu.school.jpa.TimeScheduleUnit;
import edu.school.jpa.User;

import java.lang.reflect.Method;

import java.security.Principal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.Map;

import javax.faces.context.FacesContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class JavaServiceFacade {
  private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SchedulePUFacade");

  public JavaServiceFacade() {
  }

  public static void main(String [] args) {
    final JavaServiceFacade javaServiceFacade = new JavaServiceFacade();
    //  TODO:  Call methods on javaServiceFacade here...
  }

  protected EntityManager getEntityManager() {
    return emf.createEntityManager();
  }

  public Object queryByRange(String jpqlStmt, int firstResult, int maxResults) {
    Query query = getEntityManager().createQuery(jpqlStmt);
    if (firstResult > 0) {
      query = query.setFirstResult(firstResult);
    }
    if (maxResults > 0) {
      query = query.setMaxResults(maxResults);
    }
    return query.getResultList();
  }

  public List findRange(int[] range, Class entityClass) {
      javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
      cq.select(cq.from(entityClass));
      javax.persistence.Query q = getEntityManager().createQuery(cq).setParameter("userId", ControllerUtils.getCurrentUser().getId());
      q.setMaxResults(range[1] - range[0]);
      q.setFirstResult(range[0]);
      return q.getResultList();
  }

  public int count(Class entityClass) {
      javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
      javax.persistence.criteria.Root rt = cq.from(entityClass);
      cq.select(getEntityManager().getCriteriaBuilder().count(rt));
      javax.persistence.Query q = getEntityManager().createQuery(cq);
      return ((Long) q.getSingleResult()).intValue();
  }
  
  private Integer getUserId() {
    FacesContext adfCtx = FacesContext.getCurrentInstance();
    Map<String,Object> session = adfCtx.getExternalContext().getSessionMap();
    Principal currentUser = (Principal)session.get("currentPrincipal");
    Object currentUserEntity = session.get("currentUser");
    if (currentUserEntity == null && currentUser != null) {
      currentUserEntity = getEntityManager().createNamedQuery("User.findLoginName").
                setParameter("loginName", currentUser.getName()).getSingleResult();
      if (currentUserEntity != null) {
        session.put("currentUser",currentUserEntity);
      }
    }
    Integer userId = null; 
    if (currentUserEntity != null) {
      try {
        Method getId = currentUserEntity.getClass().getDeclaredMethod("getId", new Class[0]);
        userId = (Integer)getId.invoke(currentUserEntity, new Object[0]);
      } catch (Exception e) {
        e.printStackTrace();
        userId = null;
      }
    }
    return userId;
  }
  

  public Object _persistEntity(Object entity) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      if (UserIdJpaEntity.class.isInstance(entity)) {
        Integer userId = getUserId();
        ((UserIdJpaEntity)entity).setUserId(userId);
      }
      try {
        et.begin();
        em.persist(entity);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          entity = null;
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
    return entity;
  }

  public Classroom persistClassroom(Classroom classroom) {
    return (Classroom)_persistEntity(classroom);
  }

  public List<TimeScheduleUnit> getTimeScheduleUnits(int version) {
    final EntityManager em = getEntityManager();
    List<TimeScheduleUnit> list =
      (List<TimeScheduleUnit>)em.createNamedQuery("TimeScheduleUnit.findByVersion").setParameter("userId", getUserId()).
                                    setParameter("version", version).getResultList();
    return list;
  }
  
  public List<TimeScheduleUnit> getTimeScheduleUnits() {
    final EntityManager em = getEntityManager();
    Integer version =
      (Integer)em.createNamedQuery("TimeScheduleUnit.findMaxVersion").setParameter("userId", getUserId()).getSingleResult();
    return getTimeScheduleUnits(version != null ? version : 1);
  }
  
  public List<TimeScheduleMaster> getTimeScheduleMaster() {
    final EntityManager em = getEntityManager();
    List<TimeScheduleMaster> list =
      (List<TimeScheduleMaster>)em.createNamedQuery("TimeScheduleMaster.findAll").setParameter("userId", getUserId()).getResultList();
    return list;
  }

  public Integer getTimeScheduleMaxVersion() {
    final EntityManager em = getEntityManager();
    Integer max =
      (Integer)em.createNamedQuery("TimeScheduleMaster.maxVersion").setParameter("userId", getUserId()).getSingleResult();
    return max == null ? 0 : max;
  }
  
  public Object _mergeEntity(Object entity) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        em.merge(entity);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          entity = null;
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
    return entity;
  }
  
  public Object find(Object id, Class entityClass) {
      return getEntityManager().find(entityClass, id);
  }

  public Classroom mergeClassroom(Classroom classroom) {
    return (Classroom)_mergeEntity(classroom);
  }

  public void removeClassroom(Classroom classroom) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        classroom = em.find(Classroom.class, classroom.getId());
        em.remove(classroom);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from Classroom o</code> */
  public List<Classroom> getClassroomFindAll() {
    return getEntityManager().createNamedQuery("Classroom.findAll").setParameter("userId", getUserId()).getResultList();
  }

  public Availability persistAvailability(Availability availability) {
    return (Availability)_persistEntity(availability);
  }

  public Availability mergeAvailability(Availability availability) {
    return (Availability)_mergeEntity(availability);
  }

  public void removeAvailability(Availability availability) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        availability = em.find(Availability.class, availability.getId());
        em.remove(availability);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from Availability o</code> */
  public List<Availability> getAvailabilityFindAll() {
    return getEntityManager().createNamedQuery("Availability.findAll").setParameter("userId", getUserId()).getResultList();
  }

  public List<Availability> getAvailabilityByOwner(String key, String ownerType) {
    Query q = getEntityManager().createNamedQuery("Availability.findByOwner");
    return q.setParameter("owner", Integer.parseInt(key)).setParameter("ownerType", ownerType).getResultList();
  }

  public ClassGroupSubjectMap persistClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    return (ClassGroupSubjectMap)_persistEntity(classGroupSubjectMap);
  }

  public ClassGroupSubjectMap mergeClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    return (ClassGroupSubjectMap)_mergeEntity(classGroupSubjectMap);
  }

  public void removeClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        classGroupSubjectMap = em.find(ClassGroupSubjectMap.class, new ClassGroupSubjectMapPK(classGroupSubjectMap.getClass_group_id(), classGroupSubjectMap.getSubject_id()));
        em.remove(classGroupSubjectMap);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from ClassGroupSubjectMap o</code> */
  public List<ClassGroupSubjectMap> getClassGroupSubjectMapFindAll() {
    return getEntityManager().createNamedQuery("ClassGroupSubjectMap.findAll").getResultList();
  }

  public ClassSubjectCount persistClassSubjectCount(ClassSubjectCount classSubjectCount) {
    return (ClassSubjectCount)_persistEntity(classSubjectCount);
  }

  public ClassSubjectCount mergeClassSubjectCount(ClassSubjectCount classSubjectCount) {
    return (ClassSubjectCount)_mergeEntity(classSubjectCount);
  }

  public void removeClassSubjectCount(ClassSubjectCount classSubjectCount) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        classSubjectCount = em.find(ClassSubjectCount.class, new ClassSubjectCountPK(classSubjectCount.getClass_group_id(), classSubjectCount.getSubject_id()));
        em.remove(classSubjectCount);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from ClassSubjectCount o</code> */
  public List<ClassSubjectCount> getClassSubjectCountFindAll() {
    return getEntityManager().createNamedQuery("ClassSubjectCount.findAll").getResultList();
  }

  public TeacherSubjectMap persistTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    return (TeacherSubjectMap)_persistEntity(teacherSubjectMap);
  }

  public TeacherSubjectMap mergeTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    return (TeacherSubjectMap)_mergeEntity(teacherSubjectMap);
  }

  public void removeTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        teacherSubjectMap = em.find(TeacherSubjectMap.class, new TeacherSubjectMapPK(teacherSubjectMap.getSubject_id(), teacherSubjectMap.getTeacher_id()));
        em.remove(teacherSubjectMap);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from TeacherSubjectMap o</code> */
  public List<TeacherSubjectMap> getTeacherSubjectMapFindAll() {
    return getEntityManager().createNamedQuery("TeacherSubjectMap.findAll").getResultList();
  }

  public User persistUser(User user) {
    return (User)_persistEntity(user);
  }

  public User mergeUser(User user) {
    return (User)_mergeEntity(user);
  }

  public void removeUser(User user) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        user = em.find(User.class, user.getId());
        em.remove(user);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from User o</code> */
  public List<User> getUserFindAll() {
    return getEntityManager().createNamedQuery("User.findAll").getResultList();
  }

  public Teacher persistTeacher(Teacher teacher) {
    return (Teacher)_persistEntity(teacher);
  }

  public Teacher mergeTeacher(Teacher teacher) {
    return (Teacher)_mergeEntity(teacher);
  }

  public void remove(AbstractJpaEntity entity, Class entityClass) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      Object toRemove;
      try {
        et.begin();
        toRemove = em.find(entityClass, entity.getEID());
        em.remove(toRemove);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }
  
  public void removeTeacher(Teacher teacher) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        teacher = em.find(Teacher.class, teacher.getId());
        em.remove(teacher);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from Teacher o</code> */
  public List<Teacher> getTeacherFindAll() {
    return getEntityManager().createNamedQuery("Teacher.findAll").setParameter("userId", getUserId()).getResultList();
  }

  public Subject persistSubject(Subject subject) {
    return (Subject)_persistEntity(subject);
  }

  public Subject mergeSubject(Subject subject) {
    return (Subject)_mergeEntity(subject);
  }

  public void removeSubject(Subject subject) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        subject = em.find(Subject.class, subject.getId());
        em.remove(subject);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from Subject o</code> */
  public List<Subject> getSubjectFindAll() {
    return getEntityManager().createNamedQuery("Subject.findAll").setParameter("userId", getUserId()).getResultList();
  }

  public ClassGroup persistClassGroup(ClassGroup classGroup) {
    return (ClassGroup)_persistEntity(classGroup);
  }

  public ClassGroup mergeClassGroup(ClassGroup classGroup) {
    return (ClassGroup)_mergeEntity(classGroup);
  }

  public void removeClassGroup(ClassGroup classGroup) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        classGroup = em.find(ClassGroup.class, classGroup.getId());
        em.remove(classGroup);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from ClassGroup o</code> */
  public List<ClassGroup> getClassGroupFindAll() {
    return getEntityManager().createNamedQuery("ClassGroup.findAll").setParameter("userId", getUserId()).getResultList();
  }

  public Lesson persistLesson(Lesson lesson) {
    return (Lesson)_persistEntity(lesson);
  }

  public Lesson mergeLesson(Lesson lesson) {
    return (Lesson)_mergeEntity(lesson);
  }

  public void removeLesson(Lesson lesson) {
    final EntityManager em = getEntityManager();
    try {
      final EntityTransaction et = em.getTransaction();
      try {
        et.begin();
        lesson = em.find(Lesson.class, lesson.getId());
        em.remove(lesson);
        et.commit();
      } finally {
        if (et != null && et.isActive()) {
          et.rollback();
        }
      }
    } finally {
      if (em != null && em.isOpen()) {
        em.close();
      }
    }
  }

  /** <code>select o from Lesson o</code> */
  public List<Lesson> getLessonFindAll() {
    List<Lesson> list;
    list = getEntityManager().createNamedQuery("Lesson.findAll").setParameter("userId", getUserId()).getResultList();
    Collections.sort(list, new Comparator() {
        public int compare(Object o1, Object o2) {
          Lesson a = (Lesson)o1;
          Lesson b = (Lesson)o2;
          String strAH = a.getStart().substring(0, a.getStart().indexOf(':'));
          String strAM = a.getStart().substring(a.getStart().indexOf(':')+1);
          String strBH = b.getStart().substring(0, b.getStart().indexOf(':'));
          String strBM = b.getStart().substring(b.getStart().indexOf(':')+1);
          int ah = Integer.parseInt(strAH);
          int am = Integer.parseInt(strAM);
          int bh = Integer.parseInt(strBH);
          int bm = Integer.parseInt(strBM);
          return (ah != bh) ? ((Integer)ah).compareTo(bh) : ((Integer)am).compareTo(bm);
        }
      });
    return list;
  }
  
  
  public List<Availability> findByOwner(String key, String ownerType, User user) {
      Query q = getEntityManager().createNamedQuery("Availability.findByOwner");
      return q.setParameter("owner", Integer.parseInt(key)).setParameter("ownerType", ownerType).setParameter("userId", user.getId()).getResultList();
  }
  
  public int getMaxOwner(String ownerType, User user) {
      Query q = getEntityManager().createNamedQuery("Availability.maxOwner");
      Object mo = q.setParameter("ownerType", ownerType).setParameter("userId", user.getId()).getSingleResult();
      return mo == null ? 0 : (Integer)mo;
  }

  public void setSubjectCount(ClassGroup group, Subject subject, int subjectCount) {
      ClassSubjectCount csCount = getSubjectCount(group, subject);
      if (csCount == null) {
          csCount = new ClassSubjectCount(group.getId(), subjectCount, subject.getId());
      }
      if (csCount != null) {
          csCount.setSubject_count(subjectCount);
          getEntityManager().merge(csCount);
      }
  }
  
  public ClassSubjectCount getSubjectCount(ClassGroup group, Subject subject) {
      ClassSubjectCountPK pk = new ClassSubjectCountPK(group.getId(), subject.getId());
      ClassSubjectCount csCount = getEntityManager().find(ClassSubjectCount.class, pk);
      return csCount;
  }
  
  public void removeSubjectCount(ClassGroup group, Subject subject) {
      ClassSubjectCount csCount = getSubjectCount(group, subject);
      if (csCount != null) {
          getEntityManager().remove(csCount);
      }
  }
}
