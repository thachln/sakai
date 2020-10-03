<!-- 2. normal flow -->
<h:panelGrid columns="6" border="0" rendered="#{!(delivery.pageContents.isNoParts && delivery.navigation eq '1')}">
  <%-- PREVIOUS --%>
  <h:panelGrid columns="1" border="0">
	<h:commandButton id="previous_top" type="submit" value="#{deliveryMessages.previous}"
    action="#{delivery.previous}"
    disabled="#{!delivery.previous}" 
	rendered="#{(delivery.actionString=='previewAssessment'
                 || delivery.actionString=='takeAssessment'
                 || delivery.actionString=='takeAssessmentViaUrl')
              && delivery.navigation ne '1' && ((delivery.previous && delivery.doContinue) || (!delivery.previous && delivery.doContinue) || (delivery.previous && !delivery.doContinue))}" />
  </h:panelGrid>

  <%-- NEXT --%>
  <h:panelGrid columns="1" border="0" columnClasses="act">
    <h:commandButton id="next1_top" type="submit" value="#{commonMessages.action_next}"
    action="#{delivery.next_page}" disabled="#{!delivery.doContinue}"
	rendered="#{(delivery.actionString=='previewAssessment'
                 || delivery.actionString=='takeAssessment'
                 || delivery.actionString=='takeAssessmentViaUrl')
              && (delivery.previous && !delivery.doContinue)}" />

    <h:commandButton id="nextTop" type="submit" value="#{commonMessages.action_next}"
    action="#{delivery.next_page}"
	rendered="#{(delivery.actionString=='previewAssessment'
                 || delivery.actionString=='takeAssessment'
                 || delivery.actionString=='takeAssessmentViaUrl')
              && delivery.doContinue}" />

  </h:panelGrid>


  <h:panelGrid columns="1" border="0">
           <h:outputText value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" escape="false" />
  </h:panelGrid>

  <%-- SAVE --%>
  <h:panelGrid columns="1" border="0" >
  <h:commandButton id="save_top" type="submit" value="#{commonMessages.action_save}"
    action="#{delivery.save_work}" rendered="#{delivery.actionString=='previewAssessment'
                 || delivery.actionString=='takeAssessment'
                 || delivery.actionString=='takeAssessmentViaUrl'}" />
  </h:panelGrid>

  <h:panelGrid columns="1"  border="0">
  <%-- EXIT --%>
  <h:commandButton type="submit" value="#{deliveryMessages.button_exit}"
    action="#{delivery.saveAndExit}" id="saveAndExit_top"
    rendered="#{(delivery.actionString=='previewAssessment'  
                 || delivery.actionString=='takeAssessment'
                 || (delivery.actionString=='takeAssessmentViaUrl' && !delivery.anonymousLogin))
              && delivery.navigation ne '1' && !delivery.hasTimeLimit}"  
    />

  <%-- SAVE AND EXIT DURING PAU WITH ANONYMOUS LOGIN--%>
  <h:commandButton  type="submit" value="#{deliveryMessages.button_exit}"
    action="#{delivery.saveAndExit}" id="quit_top"
    rendered="#{(delivery.actionString=='takeAssessmentViaUrl' && delivery.anonymousLogin) && !delivery.hasTimeLimit}"
    /> 

  <%-- SAVE AND EXIT FOR LINEAR ACCESS --%>
  <h:commandButton type="submit" value="#{deliveryMessages.button_exit}"
    action="#{delivery.saveAndExit}" id="saveAndExit2_top"
    rendered="#{(delivery.actionString=='previewAssessment'  
                 ||delivery.actionString=='takeAssessment'
                 || (delivery.actionString=='takeAssessmentViaUrl' && !delivery.anonymousLogin))
            && delivery.navigation eq '1' && delivery.doContinue && !delivery.hasTimeLimit}"
    />
  </h:panelGrid>

  <h:panelGrid columns="2" width="100%" border="0" columnClasses="act">
  <%-- SUBMIT FOR GRADE --%>
  <h:commandButton id="submitForGrade_top" type="submit" value="#{deliveryMessages.button_submit_grading}"
    action="#{delivery.confirmSubmit}" styleClass="active"
    rendered="#{(delivery.actionString=='takeAssessment' ||delivery.actionString=='takeAssessmentViaUrl' || delivery.actionString=='previewAssessment') 
             && delivery.navigation ne '1' 
             && !delivery.doContinue}"
    />

  <%-- SUBMIT FOR GRADE DURING PAU --%>
  <h:commandButton type="submit" value="#{deliveryMessages.button_submit}"
    action="#{delivery.confirmSubmit}"  id="submitForm1_top" styleClass="active"
    rendered="#{delivery.actionString=='takeAssessmentViaUrl' && delivery.doContinue && delivery.anonymousLogin}"
    />

  <%-- SUBMIT FOR GRADE FOR LINEAR ACCESS --%>
  <h:commandButton type="submit" value="#{deliveryMessages.button_submit_grading}"
      action="#{delivery.confirmSubmit}"  id="submitForm_top" styleClass="active"
      rendered="#{(delivery.actionString=='takeAssessment'
                   || delivery.actionString=='takeAssessmentViaUrl'
				   || delivery.actionString=='previewAssessment')
				   && delivery.navigation eq '1' && !delivery.doContinue}" 
      />

  <%-- SUBMIT FOR DUE OR RETRACT DATE --%>
  <h:commandButton id="submitNoCheck_top" type="submit" styleClass="hidden active" action="#{delivery.submitFromTimeoutPopup}" value="" />

  </h:panelGrid>
</h:panelGrid>

