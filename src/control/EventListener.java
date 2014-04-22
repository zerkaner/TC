package control;

import model.Roxel;
import model.Roxel.DIRECTION;

import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
public class EventListener {

		// Define the event we are interested in
		@EventTemplate
		Roxel unprocessedData() {
			Roxel template = new Roxel();
			template.setDirection(DIRECTION.TODECIDE);
			return template;
		}

		@SpaceDataEvent
		public Roxel eventListener(Roxel event) {
			System.out.println("Processing ... "+event);

			return event;
		}
	
}
