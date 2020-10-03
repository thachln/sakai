package m.k.s.sakai.app.question.logic;

import java.util.List;

import org.apache.log4j.Logger;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.assessment.data.ifc.questionpool.QuestionPoolDataIfc;
import org.sakaiproject.tool.assessment.shared.api.questionpool.QuestionPoolServiceAPI;
import org.sakaiproject.user.api.UserDirectoryService;

import lombok.Getter;
import lombok.Setter;

/**
 * Implementation of {@link SakaiProxy}
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class SakaiProxyImpl implements SakaiProxy {

    private static final Logger LOG = Logger.getLogger(SakaiProxyImpl.class);

    /**
     * {@inheritDoc}
     */
    public String getCurrentSiteId() {
        return toolManager.getCurrentPlacement().getContext();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentUserId() {
        return sessionManager.getCurrentSessionUserId();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentUserDisplayName() {
        return userDirectoryService.getCurrentUser().getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuperUser() {
        return securityService.isSuperUser();
    }

    /**
     * {@inheritDoc}
     */
    public void postEvent(String event, String reference, boolean modify) {
        eventTrackingService.post(eventTrackingService.newEvent(event, reference, modify));
    }

    /**
     * {@inheritDoc}
     */
    public String getSkinRepoProperty() {
        return serverConfigurationService.getString("skin.repo");
    }

    /**
     * {@inheritDoc}
     */
    public String getToolSkinCSS(String skinRepo) {

        String skin = siteService.findTool(sessionManager.getCurrentToolSession().getPlacementId()).getSkin();

        if (skin == null) {
            skin = serverConfigurationService.getString("skin.default");
        }

        return skinRepo + "/" + skin + "/tool.css";
    }

    /**
     * init - perform any actions required here for when this bean starts up
     */
    public void init() {
        LOG.info("init");
    }

    @Getter
    @Setter
    private ToolManager toolManager;

    @Getter
    @Setter
    private SessionManager sessionManager;

    @Getter
    @Setter
    private UserDirectoryService userDirectoryService;

    @Getter
    @Setter
    private SecurityService securityService;

    @Getter
    @Setter
    private EventTrackingService eventTrackingService;

    @Getter
    @Setter
    private ServerConfigurationService serverConfigurationService;

    @Getter
    @Setter
    private SiteService siteService;

    @Getter
    @Setter
    private QuestionPoolServiceAPI questionPoolService;

    public List<QuestionPoolDataIfc> getPools() {
        List<QuestionPoolDataIfc> pools;

        String currentUserId = getCurrentUserId();
        LOG.info("currentUserId=" + currentUserId);
        
        pools = questionPoolService.getAllPools(currentUserId);
//        List<QuestionPoolDataIfc> pools = questionPoolService.getPoolIdsByAgent(currentUserId);
        
        // Get root Pools by remove all subpools recursively
        if (pools != null) {
            // Scan from right to left
            Long parrentPoolId;
            for (int i = pools.size() - 1; i >= 0; i--) {
                parrentPoolId = pools.get(i).getParentPoolId();
                
                LOG.info("parent poolId=" + parrentPoolId + "; pool Title:" + pools.get(i).getTitle() + ";poolId:" + pools.get(i).getQuestionPoolId());
                if (parrentPoolId > 0) {
                    pools.remove(i);
                }
            }
        }
        
        return pools;
    }

    /* (non-Javadoc)
     * @see m.k.s.sakai.app.question.logic.SakaiProxy#getPools(java.lang.Long)
     */
    public List<QuestionPoolDataIfc> getPools(Long poolId) {
        String currentUserId = getCurrentUserId();
        LOG.info("Get sub pools of '" + poolId + ";currentUserId=" + currentUserId);
        
        List<QuestionPoolDataIfc> pools = questionPoolService.getSubPools(poolId);
        
        return pools;
    }
}
