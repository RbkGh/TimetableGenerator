/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.view;

import edu.school.schedule.PreconditionChecker;
import edu.school.schedule.Scheduler;
import edu.school.schedule.SchedulingUnit;
import edu.school.schedule.TimeSchedule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.school.jpa.Classroom;

import edu.school.jpa.TimeScheduleMaster;

import java.sql.Timestamp;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import org.apache.myfaces.trinidad.context.PageFlowScopeProvider;
import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.DisclosureEvent;

/**
 *
 * @author Penguin
 */
public class ScheduleController extends CRUDBean implements Serializable{

    private Scheduler scheduler;
    private TimeSchedule timeSchedule;
    
    private String scheduleDay;
    private SchedulingUnit[][] daySchedule;
  private RichPopup storeDbPopup;
  private RichTable storeDbTable;
  private RichPopup loadDbPopup;
  private RichPopup downloadPopup;
  private RichTable loadDbTable;

  /**
   * Creates a new instance of ScheduleController
   */
    public ScheduleController() {
        scheduleDay = "Monday";
    }
    
    /**
     * Action handler for initialization of schedule
     * @return 
     */
    public String initialize() {
        scheduler = new Scheduler();
        PreconditionChecker preCheck = new PreconditionChecker(scheduler);
        
        boolean isOK = preCheck.test();
      scheduler.addError(isOK?"All preconditions checks passed.":"Some precondition checks failed.");
        if (true) {
      Map<String, Object> flowScope = RequestContext.getCurrentInstance().getPageFlowScope();
      String actionType = (String)flowScope.get("actionType");
      timeSchedule = new TimeSchedule(scheduler);
          int version = getEjbFacade().getTimeScheduleMaxVersion();
          if (version > 0 && !"generate".equalsIgnoreCase(actionType)) {
            timeSchedule.load(version);
          } else {
            timeSchedule.intializeCube();
          }
        }
        return "timeSchedule";
    }
    
    public List<String> getErrors() {
        if (scheduler == null) {
            ArrayList<String> errs = new ArrayList<String>(1);
            errs.add("Schedule was not yet initialized.");
            return errs;
        }
        List<String> errs = scheduler.getErrors();
        if (errs.isEmpty())
            errs.add("No errros.");
        return errs;
    }
    
    public String getErrorsFormatted() {
      StringBuilder html = new StringBuilder("<div>");
      for (String e : getErrors()) {
        html.append("<div>").append(e).append("</div>");
      }
      html.append("</div>");
      return html.toString();
    }
    
    public String showMonday() {
        scheduleDay = "Monday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[0];
        return null;
    }

    public String showTuesday() {
        scheduleDay = "Tuesday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[1];
        return null;
    }

    public String showWednesday() {
        scheduleDay = "Wednesday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[2];
        return null;
    }

    public String showThursday() {
        scheduleDay = "Thursday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[3];
        return null;
    }

    public String showFriday() {
        scheduleDay = "Friday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[4];
        return null;
    }

    public String showSaturday() {
        scheduleDay = "Saturday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[5];
        return null;
    }

    public String showSunday() {
        scheduleDay = "Sunday";
        if (timeSchedule != null)
            daySchedule = timeSchedule.getCube()[6];
        return null;
    }

    /**
     * @return the scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * @return the timeSchedule
     */
    public TimeSchedule getTimeSchedule() {
        return timeSchedule;
    }

    /**
     * @param timeSchedule the timeSchedule to set
     */
    public void setTimeSchedule(TimeSchedule timeSchedule) {
        this.timeSchedule = timeSchedule;
    }

    /**
     * @return the scheduleDay
     */
    public String getScheduleDay() {
        return scheduleDay;
    }

    /**
     * @param scheduleDay the scheduleDay to set
     */
    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    /**
     * @return the daySchedule
     */
    public ArrayList<ArrayList<SchedulingUnit>> getDaySchedule() {
        ArrayList<ArrayList<SchedulingUnit>> dayScheduleUI = new ArrayList<ArrayList<SchedulingUnit>>();
        if (daySchedule == null)
            daySchedule = timeSchedule.getCube()[0];
        if (daySchedule != null) {
            for (int i = 0; i < daySchedule.length; i++) {
                SchedulingUnit[] lessonSchedule = daySchedule[i];  // get all rooms scheduled for this slot
                ArrayList<SchedulingUnit> list = new ArrayList<SchedulingUnit>();
                list.add(new SchedulingUnit(null,null,null,0,scheduler.getLessons().get(i),null));
                list.addAll(Arrays.asList(lessonSchedule));
                dayScheduleUI.add(list);
            }
        }
        return dayScheduleUI;
    }

    public ArrayList<Classroom> getHeaderLine() {
      ArrayList<Classroom> list = new ArrayList<Classroom>();
      list.add(new Classroom());
      list.addAll(scheduler.getRooms());
      return list;
    }
    
    /**
     * @param daySchedule the daySchedule to set
     */
    public void setDaySchedule(SchedulingUnit[][] daySchedule) {
        this.daySchedule = daySchedule;
    }

  public void showMonday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Monday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[0];
  }

  public void showTuesday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Tuesday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[1];
  }

  public void showWednesday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Wednesday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[2];
  }

  public void showThursday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Thursday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[3];
  }

  public void showFriday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Friday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[4];
  }

  public void showSaturday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Saturday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[5];
  }

  public void showSunday(DisclosureEvent disclosureEvent) {
    scheduleDay = "Sunday";
    if (timeSchedule != null)
        daySchedule = timeSchedule.getCube()[6];
  }

  public void setStoreDbPopup(RichPopup storeDbPopup) {
    this.storeDbPopup = storeDbPopup;
  }

  public RichPopup getStoreDbPopup() {
    return storeDbPopup;
  }

  public void storeDbFetchListener(PopupFetchEvent popupFetchEvent) {
    storedVersionComment = null;
  }
  
  public List<TimeScheduleMaster> getStoredVersions() {
    List<TimeScheduleMaster> list = getEjbFacade().getTimeScheduleMaster();
    return list;
  }
  
  public void storeToDB(ActionEvent event) {
    String sourceId = ((UIComponent)event.getSource()).getId();
    if (!sourceId.startsWith("cancel")) {
      TimeScheduleMaster data = (TimeScheduleMaster)storeDbTable.getSelectedRowData();
      int version = getEjbFacade().getTimeScheduleMaxVersion();
      if (data == null) {
        data = new TimeScheduleMaster();
        data.setVersion(version+1);
        data.setOrganization_id(1);
      }
      data.setComment(storedVersionComment);
      data.setDate_created(new Timestamp(System.currentTimeMillis()));
      if (data.getEid() != null)
        getEjbFacade()._mergeEntity(data);
      else 
        getEjbFacade()._persistEntity(data);
      if (sourceId.startsWith("save")) {
        timeSchedule.persist(version+1);
      } else if (sourceId.startsWith("update")) {
        timeSchedule.persist(data.getVersion());
      }
    }
    storeDbPopup.hide();
  }

  public void setStoreDbTable(RichTable storeDbTable) {
    this.storeDbTable = storeDbTable;
  }

  public RichTable getStoreDbTable() {
    return storeDbTable;
  }
  
  private String storedVersionComment;

  public String getStoredVersionComment() {
    return storedVersionComment;
  }

  public void setStoredVersionComment(String storedVersionComment) {
    this.storedVersionComment = storedVersionComment;
  }

  public void setLoadDbPopup(RichPopup loadDbPopup) {
    this.loadDbPopup = loadDbPopup;
  }

  public RichPopup getLoadDbPopup() {
    return loadDbPopup;
  }

  public void loadDbFetchListener(PopupFetchEvent popupFetchEvent) {
    // Add event code here...
  }

  public void loadDbDialogListener(DialogEvent dialogEvent) {
    // Add event code here...
  }
  
  public void loadFromDB(ActionEvent event) {
    String sourceId = ((UIComponent)event.getSource()).getId();
    if (!sourceId.startsWith("cancel")) {
      TimeScheduleMaster data = (TimeScheduleMaster)loadDbTable.getSelectedRowData();
      if (data != null) {
        timeSchedule.load(data.getVersion());
      } else {
        int version = getEjbFacade().getTimeScheduleMaxVersion();
        if (version > 0)
          timeSchedule.load(version);
      }
    }
    loadDbPopup.hide();
  }

  public void setDownloadPopup(RichPopup downloadPopup) {
    this.downloadPopup = downloadPopup;
  }

  public RichPopup getDownloadPopup() {
    return downloadPopup;
  }

  public void downloadFetchListener(PopupFetchEvent popupFetchEvent) {
    // Add event code here...
  }

  public void prepareDownloadAL(ActionEvent actionEvent) {
    Map<String,Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    session.put("scheduleXML",timeSchedule.toXML());
  }

  public void setLoadDbTable(RichTable loadDbTable) {
    this.loadDbTable = loadDbTable;
  }

  public RichTable getLoadDbTable() {
    return loadDbTable;
  }
}
