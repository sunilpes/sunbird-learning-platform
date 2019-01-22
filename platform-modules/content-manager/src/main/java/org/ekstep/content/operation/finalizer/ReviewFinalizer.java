package org.ekstep.content.operation.finalizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.ekstep.common.Platform;
import org.ekstep.common.dto.Response;
import org.ekstep.common.dto.ResponseParams;
import org.ekstep.common.dto.ResponseParams.StatusType;
import org.ekstep.common.exception.ClientException;
import org.ekstep.common.exception.ServerException;
import org.ekstep.common.util.CurationUtil;
import org.ekstep.content.common.ContentErrorMessageConstants;
import org.ekstep.content.enums.ContentErrorCodeConstants;
import org.ekstep.content.enums.ContentWorkflowPipelineParams;
import org.ekstep.graph.dac.model.Node;
import org.ekstep.kafka.KafkaClient;
import org.ekstep.learning.common.enums.ContentErrorCodes;
import org.ekstep.learning.util.CloudStore;
import org.ekstep.telemetry.logger.TelemetryManager;
import org.ekstep.telemetry.util.LogTelemetryEventUtil;

public class ReviewFinalizer extends BaseFinalizer {

	/** The logger. */
	

	/** The BasePath. */
	protected String basePath;

	/** The ContentId. */
	protected String contentId;
	
	private static String actorId = "Publish Samza Job";
	private static String actorType = "System";
	private static String pdataId = "org.ekstep.platform";
	private static String pdataVersion = "1.0";
	private static String action = "publish";

	private static ObjectMapper mapper = new ObjectMapper();

	
	/**
	 * Instantiates a new ReviewFinalizer and sets the base path and current
	 * content id for further processing.
	 *
	 * @param basePath
	 *            the base path is the location for content package file
	 *            handling and all manipulations.
	 * @param contentId
	 *            the content id is the identifier of content for which the
	 *            Processor is being processed currently.
	 */
	public ReviewFinalizer(String basePath, String contentId) {
		if (!isValidBasePath(basePath))
			throw new ClientException(ContentErrorCodeConstants.INVALID_PARAMETER.name(),
					ContentErrorMessageConstants.INVALID_CWP_CONST_PARAM + " | [Path does not Exist.]");
		if (StringUtils.isBlank(contentId))
			throw new ClientException(ContentErrorCodeConstants.INVALID_PARAMETER.name(),
					ContentErrorMessageConstants.INVALID_CWP_CONST_PARAM + " | [Invalid Content Id.]");
		this.basePath = basePath;
		this.contentId = contentId;
	}

	/**
	 * finalize()
	 *
	 * @param parameterMap
	 *            the parameterMap
	 * 
	 *            checks if Node, ecrfType,ecmlType exists in the parameterMap
	 *            else throws ClientException Output only ECML format create
	 *            'artifactUrl' Get Content String write ECML File Create 'ZIP'
	 *            Package Upload Package Upload to AWS/Azure Set artifact file For Node
	 *            Download App Icon and create thumbnail Set Package Version
	 *            Create ECAR Bundle Delete local compressed artifactFile
	 *            Populate Fields and Update Node
	 * @return the response
	 */
	public Response finalize(Map<String, Object> parameterMap) {
		TelemetryManager.log("Parameter Map: ", parameterMap);
		Response response;
		if (null == parameterMap)
			throw new ClientException(ContentErrorCodeConstants.INVALID_PARAMETER.name(),
					ContentErrorMessageConstants.INVALID_PARAMETER_MAP + " | [Parameter Map Cannot be 'null']");
		
		Node node = (Node) parameterMap.get(ContentWorkflowPipelineParams.node.name());
		if (null == node)
			throw new ClientException(ContentErrorCodeConstants.INVALID_PARAMETER.name(),
					ContentErrorMessageConstants.INVALID_CWP_FINALIZE_PARAM + " | [Invalid or null Node.]");
		
		Boolean isPublishOperation = (Boolean) parameterMap.get(ContentWorkflowPipelineParams.isPublishOperation.name());
		if (BooleanUtils.isTrue(isPublishOperation)) {
			//TelemetryManager.info("Changing the Content Status to 'Pending' for content id: " + node.getIdentifier());
			String publishType = (String)node.getMetadata().get("publish_type");
			node.getMetadata().remove("publish_type");
			try {
				pushInstructionEvent(node, publishType);
				TelemetryManager.info("Content: " + node.getIdentifier() + " pushed to kafka for publish operation.");
			} catch (Exception e) {
				throw new ServerException(ContentErrorCodes.ERR_CONTENT_PUBLISH.name(),
						"Error occured during content publish", e);
			}
			node.getMetadata().put("publish_type", publishType); //Added for executing publish operation locally
			
			response = new Response();
			ResponseParams param = new ResponseParams();
			param.setStatus(StatusType.successful.name());
			response.setParams(param);
			response.put("publishStatus", "Publish Event for Content Id '" + node.getIdentifier() + "' is pussed Successfully!");
			response.put("node_id", node.getIdentifier());
		} else {
			String prevState = (String) node.getMetadata().get(ContentWorkflowPipelineParams.status.name());
			if(StringUtils.equalsIgnoreCase(prevState, ContentWorkflowPipelineParams.FlagDraft.name())){
				TelemetryManager.info("Setting status to flagReview from previous state : " + prevState + " for content id: " + node.getIdentifier());
				node.getMetadata().put(ContentWorkflowPipelineParams.status.name(), ContentWorkflowPipelineParams.FlagReview.name());
			}else {
				TelemetryManager.info("Changing the Content Status to 'Review' for content id: " + node.getIdentifier());
				node.getMetadata().put(ContentWorkflowPipelineParams.status.name(), ContentWorkflowPipelineParams.Review.name());
			}
			node.getMetadata().put(ContentWorkflowPipelineParams.reviewError.name(), null);
			Node newNode = new Node(node.getIdentifier(), node.getNodeType(), node.getObjectType());
			newNode.setGraphId(node.getGraphId());
			newNode.setMetadata(node.getMetadata());

			response = updateContentNode(contentId, newNode, null);

			if(!checkError(response)){
				String identifier = newNode.getIdentifier();
				String mimeType= (String) newNode.getMetadata().get("mimeType");
				String autoCurationEnabled = (String) newNode.getMetadata().get("autoCurationEnabled");
				//Added passport key to skip versionKey.
				String passportKey = Platform.config.getString("graph.passport.key.base");

				if("application/vnd.ekstep.ecml-archive".equalsIgnoreCase(mimeType) && "yes".equalsIgnoreCase(autoCurationEnabled)){
					ExecutorService pool = null;
					try {
						pool = Executors.newFixedThreadPool(1);
						pool.execute(new Runnable() {
							@Override
							public void run() {
								//update the curation status
								newNode.getMetadata().put("versionKey",passportKey);
								newNode.getMetadata().put("curationStatus","Processing");
								Response updateResponse = updateContentNode(contentId, newNode, null);
								if(!checkError(updateResponse)){
									TelemetryManager.log("Auto Curation is Started Now for Content : "+identifier);
									System.out.println("Starting Auto Curation for Content Id : "+identifier);

									String contentBody = getContentBody(identifier);
									TelemetryManager.log("original contentBody :"+contentBody);
									System.out.println("original contentBody:::::::"+contentBody);

									String extractedTextData = CurationUtil.getText(contentBody);
									TelemetryManager.log("Extracted Text: "+extractedTextData);

									System.out.println("Extracted Text: "+extractedTextData);

									//check profanity
									List<String> badWords = CurationUtil.checkProfanity(extractedTextData);

									if(!badWords.isEmpty()){
										String profanityCheckError ="Objectionable Words Found in Content :"+badWords;
										newNode.getMetadata().put("profanityCheckError",profanityCheckError);
									}

									//TODO: Get Suggested Keywords and Perform Content Update together

									//update the curation status
									newNode.getMetadata().put("versionKey",passportKey);
									newNode.getMetadata().put("curationStatus","Finished");
									Response curateResponse = updateContentNode(contentId, newNode, null);
									TelemetryManager.log("Auto Curation is Completed for Content : "+identifier);
								}

							}
						});
					} catch (Exception e) {
						TelemetryManager.error("Error while doing auto curation", e);
					} finally {
						if (null != pool)
							pool.shutdown();
					}
				}
			}
		}
		
		return response;
	}
	
	private void pushInstructionEvent(Node node, String publishType) throws Exception{
		Map<String,Object> actor = new HashMap<String,Object>();
		Map<String,Object> context = new HashMap<String,Object>();
		Map<String,Object> object = new HashMap<String,Object>();
		Map<String,Object> edata = new HashMap<String,Object>();
		
		generateInstructionEventMetadata(actor, context, object, edata, node.getMetadata(), contentId, publishType);
		String beJobRequestEvent = LogTelemetryEventUtil.logInstructionEvent(actor, context, object, edata);
		String topic = Platform.config.getString("kafka.topics.instruction");
		if(StringUtils.isBlank(beJobRequestEvent)) {
			TelemetryManager.error("Instruction event is not generated properly. # beJobRequestEvent : " + beJobRequestEvent);
			throw new ClientException("BE_JOB_REQUEST_EXCEPTION", "Event is not generated properly.");
		}
		if(StringUtils.isNotBlank(topic)) {
			KafkaClient.send(beJobRequestEvent, topic);
		} else {
			TelemetryManager.error("Invalid topic id. # topic : " + topic);
			throw new ClientException("BE_JOB_REQUEST_EXCEPTION", "Invalid topic id.");
		}
	}
	
	private void generateInstructionEventMetadata(Map<String,Object> actor, Map<String,Object> context, 
			Map<String,Object> object, Map<String,Object> edata, Map<String, Object> metadata, String contentId, String publishType) {
		
		actor.put("id", actorId);
		actor.put("type", actorType);
		
		context.put("channel", metadata.get("channel")); 
		Map<String, Object> pdata = new HashMap<>();
		pdata.put("id", pdataId); 
		pdata.put("ver", pdataVersion);
		context.put("pdata", pdata);
		if (Platform.config.hasPath("cloud_storage.env")) {
			String env = Platform.config.getString("cloud_storage.env");
			context.put("env", env);
		}
		
		object.put("id", contentId);
		object.put("ver", metadata.get("versionKey"));

		Map<String, Object> instructionEventMetadata = new HashMap<>();
		instructionEventMetadata.put("pkgVersion", metadata.get("pkgVersion"));
		instructionEventMetadata.put("mimeType", metadata.get("mimeType"));
		instructionEventMetadata.put("lastPublishedBy", metadata.get("lastPublishedBy"));

		edata.put("action", action);
		edata.put("metadata", instructionEventMetadata);
		edata.put("publish_type", publishType);
		edata.put("contentType", metadata.get("contentType"));
	}
	

}
