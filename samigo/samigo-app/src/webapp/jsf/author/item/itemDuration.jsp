    <div class="form-group row">
        <h:outputLabel value="#{authorMessages.duration}" styleClass="col-md-4 col-lg-2 form-control-label"/>
        <div class="col-md-5">
          <div class="form-group row">
            <div class="col-md-12">
                <h:outputLabel value="#{authorMessages.minduration}" styleClass="col-md-2" />
                <h:inputText id="minDuration" size="4" label="#{authorMessages.minduration}" value="#{itemauthor.currentItem.minDuration}">
                    <f:validateLongRange minimum="0"/>
                </h:inputText>
                <h:message for="minDuration" styleClass="validate"/>
             </div>
           </div>
          <div class="form-group row">
            <div class="col-md-12">
                <h:outputLabel value="#{authorMessages.maxduration}" styleClass="col-md-2" />
                <h:inputText id="maxDuration" size="4" label="#{authorMessages.maxduration}" value="#{itemauthor.currentItem.maxDuration}">
                    <f:validateLongRange minimum="0"/>
                </h:inputText>
                <h:message for="maxDuration" styleClass="validate"/>
              </div>
            </div>
        </div>
    </div>