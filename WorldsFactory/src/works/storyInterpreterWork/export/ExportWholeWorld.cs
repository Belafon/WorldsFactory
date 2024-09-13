using System.Text;
using Avalonia.Platform.Storage;
using WorldsFactory.world;
using IronPython.Hosting;
using System.Text.RegularExpressions;
using Serilog;
using System.Data;
using WorldsFactory.world.events;
using WorldsFactory.works.storyInterpreterWork.export;

namespace WorldsFactory.works.storyInterpreterWork.export;
public class ExportWholeWorld
{
	IStorageFolder targetFolder;
	World world;
	public ExportWholeWorld(IStorageFolder targetFolder, World world)
	{
		this.targetFolder = targetFolder;
		this.world = world;
	}

	public void Export(out StringBuilder code)
	{
		code = new StringBuilder();
		new ExportArrayClass().Export(out var arrayClassCode);
		new ExportLogger().Export(out var loggerCode);
		new ExportClasses(world.Library).Export(out var classesCode);
		new ExportObjects(world.Objects).Export(out var objectsCode);
		new ExportEvents(world.Events).Export(out var eventsCode);
		new ExportPropertySetter().Export(out var propertySetterCode);
		new ExportEventsTree(world.Events).Export(out var eventsTreeCode);

		code.Append(loggerCode);
		code.Append(arrayClassCode);
		code.Append(propertySetterCode);
		code.Append(classesCode);
		code.Append(objectsCode);
		code.Append(eventsCode);
		code.Append(eventsTreeCode);
		
		validateCompilationErrorsCode(code);
	}

	private void validateCompilationErrorsCode(StringBuilder code)
	{
		var engine = Python.CreateEngine();

		try {
			engine.CreateScriptSourceFromString(code.ToString()).Compile();
		} catch (Microsoft.Scripting.SyntaxErrorException e) {
			int lineNum = e.Line;

			string currentClass = "";
			string currentMethod = "";
			int lineNumInMethod = 0;
			var lines = code.ToString().Split('\n');
			for(int i = 0; i < lines.Length; i++)
			{
				if(i == lineNum)
					break;

				var matchClass = Regex.Match(lines[i], @"class\s(\w+)");
				if (matchClass.Success)
					currentClass = matchClass.Groups[1].Value;
				
				var matchMethod = Regex.Match(lines[i], @"def\s(\w+)");
				if (matchMethod.Success){
					lineNumInMethod = i;
					currentMethod = matchMethod.Groups[1].Value;
				}
			}
			
			var codeAroundError = new StringBuilder("\nCode around error:\n");
			for(int i = lineNum - 5; i < lineNum + 5; i++)
			{
				if(i < 0 || i >= lines.Length)
					continue;
				codeAroundError.Append(lines[i]);
				if(i == lineNum)
					codeAroundError.Append("      <--------- ");
				codeAroundError.Append("\n");
			}

			Log.Warning("Error in class {class} in method {method} on methods line {line}: {codeAroundError}", currentClass, currentMethod, lineNum - lineNumInMethod);
			throw new SyntaxErrorException(lineNum - lineNumInMethod, e.Column, currentClass, currentMethod, e.Message + codeAroundError);
		}
	}

	public async Task ExportAndSave(string fileName){
		fileName += ".py";
		
		Export(out var code);

		await targetFolder.CreateFileAsync(fileName)
			.ContinueWith(async (file) => {
				if(file.Result is null)
					throw new FileLoadException("Cannot create new file" + fileName);
				
				await using var stream = await file.Result.OpenWriteAsync();
				using var streamWriter = new StreamWriter(stream);
				await streamWriter.WriteAsync(code);
				streamWriter.Close();
				stream.Close();
			});
	}

	public class SyntaxErrorException : Exception
	{
		public int Line { get; init; }
		public int Column { get; init; }
		public string Class { get; init; }
		public string Method { get; init; }
		public SyntaxErrorException(int line, int column, string @class, string method, string message) : base(message)
		{
			Line = line;
			Column = column;
			Class = @class;
			Method = method;
		}
		
	}

	internal class UnknownEventReferenceException : Exception
	{
		public IEvent ProblematicEvent { get; init; }
		public string UnknownEventReference { get; init; }
		public UnknownEventReferenceException(IEvent problematicEvent, string unknownEventReference) : base($"Unknown event reference {unknownEventReference} in event {problematicEvent.Id} in Event Sequence.")
		{
			this.ProblematicEvent = problematicEvent;
			this.UnknownEventReference = unknownEventReference;
		}
	}
}

internal class ExportEventsTree
{
	private IEvents events;

	public ExportEventsTree(IEvents events)
	{
		this.events = events;
	}

	internal void Export(out StringBuilder eventsTreeCode)
	{
		eventsTreeCode = new StringBuilder();
		eventsTreeCode.Append($"""
# ------------------- EVENTS TREE -------------------

from enum import Enum

class EventGraphCondition(Enum):
	MOVE_MAX_BY_ONE = 1
	MOVE_UNTIL_POSSIBLE = 2

event_tree_condition = EventGraphCondition.MOVE_MAX_BY_ONE
""");
	}
}