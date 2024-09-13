using System.Text;
using WorldsFactory.world.events;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.works.storyInterpreterWork.export;

internal class ExportEvents
{
	private IEvents events;
	private Dictionary<IEvent, List<IEvent>> eventsTree = new Dictionary<IEvent, List<IEvent>>();
	public ExportEvents(IEvents events)
	{
		this.events = events;
	}

	internal void Export(out StringBuilder code)
	{
		buildEventsGraph(events);

		code = new StringBuilder("# ------------------- EVENTS -------------------\n\n");

		var singletonsCode = new StringBuilder();
		var allEventsCode = new StringBuilder(@"
class Events:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(Events, cls).__new__(cls)
		return cls._instance
");
		foreach (var event_ in events.Collection)
		{
			singletonsCode.Append(exportEventsSingleton(event_));
			allEventsCode.Append(exportEventsInitializationCode(event_));
		}


		code.Append(singletonsCode);
		if (singletonsCode.Length > 0)
			code.Append("\n\t");

		code.Append(allEventsCode);
		code.Append("\tpass\n\n");
		code.Append("events = Events()\n\n");
		code.Append(exportEventGraphHead());
		code.Append("\n\n");
	}

	private StringBuilder exportEventsSingleton(IEvent event_)
	{
		var eventsClassName = new StringBuilder("event_" + event_.Name);

		var code = new StringBuilder($@"
class {eventsClassName}:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super({eventsClassName}, cls).__new__(cls)
		return cls._instance
		pass
	conditionMethodLoaded = True
	actionMethodLoaded = True
");
		code.Append("\t\n");
		code.Append($"\tsequence_next_events = [\n");
		foreach (var nextEvent in eventsTree[event_])
		{
			code.Append($"\t\t\"{nextEvent.Name}\",\n");
		}
		
		code.Append($"\t]\n\t\n");
		
		code.Append($"""
	def loadConditionMethod(self):
		script_path = './{event_.Name}_Condition.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.conditionMethodLoaded = True
""");
		code.Append("\n\t\n");
		code.Append($"""
	def loadActionMethod(self):
		script_path = './{event_.Name}_Action.py'
		with open(script_path, 'r') as script_file:
			script_content = script_file.read()
		exec(script_content)
		self.actionMethodLoaded = True
""");
		code.Append("\n\t\n");
		
		ExportClasses.ExportClassMethod(event_.Class, event_.Condition, out StringBuilder conditionMethodCode);
		ExportClasses.ExportClassMethod(event_.Class, event_.Action, out StringBuilder actionMethodCode);

		code.Append(conditionMethodCode);
		code.Append("\t\n");
		code.Append(actionMethodCode);
		code.Append("\n");
		return code;
	}

	private StringBuilder exportEventsInitializationCode(IEvent event_)
	{
		var code = new StringBuilder();

		return new StringBuilder($"\t{event_.Name} = event_{event_.Name}()\n");
	}


	private void buildEventsGraph(IEvents events)
	{
		foreach (var event_ in events.Collection)
		{
			eventsTree.Add(event_, new List<IEvent>());
		}

		foreach (var event_ in events.Collection)
			foreach (var childEventReference in event_.SequenceManager.Events)
			{
				var childEventAsConcept = childEventReference.TryGetConcept();
				if (childEventAsConcept is not null && childEventAsConcept is IEvent childEvent)
					eventsTree[childEvent].Add(event_);
				else throw new ExportWholeWorld.UnknownEventReferenceException(event_, childEventReference.Id);
			}
	}
	
	private StringBuilder exportEventGraphHead()
	{
		var code = new StringBuilder($"""
class event_start:
	_instance = None
	def __new__(cls):
		if cls._instance is None:
			cls._instance = super(event_start, cls).__new__(cls)
		return cls._instance
	conditionMethodLoaded = True
	actionMethodLoaded = True
	sequence_next_events = [
		
""");
		bool writeAtLeastOne = false;
		foreach (var event_ in this.events.Collection)
		{
			if(event_.SequenceManager.Events.Count == 0){
				code.Append($"\t\t\"{event_.Name}\",\n");
				writeAtLeastOne = true;
			}
		}
		if(!writeAtLeastOne)
			throw new NoEntryEventException();
		
		code.Append($"\t]\n\n");
		
		code.Append($"""
class EventTreeHead:
	last_event = event_start()
	def try_move(self):
		logging.info('Try to execute next event...')			
		for next_event_name in self.last_event.sequence_next_events:
			next_event = events.__getattribute__(next_event_name)
			if next_event.conditionMethodLoaded:
				try:
					if next_event.Condition():
						self.last_event = next_event
						logging.info('Executing next event: ' + next_event_name)
						self.last_event.Action()
						if event_tree_condition == EventGraphCondition.MOVE_UNTIL_POSSIBLE:
							self.try_move()
				except MethodRuntimeException as e:
					logging.error('MethodRuntimeException: ' + next_event_name + ' ' + e.message)
					print("_exception_")
					print(e.message)
					print("_exception_end_")

event_tree_head = EventTreeHead()
""");
		return code;
	}
			
	public class NoEntryEventException : Exception
	{
		public NoEntryEventException() : base("No entry event found for event tree.")
		{
		}
	}
}