package org.springframework.site.guides;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;

public class GettingStartedControllerTests {

	private static final String GUIDE_NAME = "rest-service";
	private static final String GUIDE_TEXT = "raw guide text";

	@Mock
	private GitHubGettingStartedService guideService;

	private ExtendedModelMap model;
	private GettingStartedController controller;

	@Before
	public void setup() {
		initMocks(this);
		controller = new GettingStartedController(guideService);
		model = new ExtendedModelMap();
	}

	@Test
	public void guideSlugInModel() {
		controller.viewGuide(GUIDE_NAME, model);
		assertThat((String) model.get("guideSlug"), is(GUIDE_NAME));
	}

	@Test
	public void guideView() {
		String view = controller.viewGuide(GUIDE_NAME, model);
		assertThat(view, is("guides/gs/guide"));
	}

	@Test
	public void guideTextInModel() {
		when(guideService.loadGuide(GUIDE_NAME)).thenReturn(new GettingStartedGuide(GUIDE_TEXT));
		controller.viewGuide(GUIDE_NAME, model);
		assertThat(((GettingStartedGuide) model.get("guide")).getContent(), is(GUIDE_TEXT));
	}

	@Test(expected = RestClientException.class)
	public void failedGuideFetch() {
		when(guideService.loadGuide(GUIDE_NAME)).thenThrow(new RestClientException("Is GitHub down?"));
		controller.viewGuide(GUIDE_NAME, model);
	}

	@Test
	public void listGuidesView(){
		String view = controller.listGuides(model);
		assertThat(view, is("guides/gs/list"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listGuidesModel(){
		List<GuideRepo> repoList = new ArrayList<GuideRepo>();
		when(guideService.listGuides()).thenReturn(repoList);
		controller.listGuides(model);
		assertThat((List<GuideRepo>) model.get("guides"), is(repoList));
	}

	@Test
	public void loadImages() {
		byte[] image = "animage".getBytes();
		when(guideService.loadImage(GUIDE_NAME, "welcome.png")).thenReturn(image);
		ResponseEntity<byte[]> responseEntity = controller.loadImage(GUIDE_NAME, "welcome.png");
		assertThat(responseEntity.getBody(), is(image));
	}
}
