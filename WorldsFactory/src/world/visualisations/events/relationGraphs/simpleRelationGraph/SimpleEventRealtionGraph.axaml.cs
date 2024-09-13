using System.Diagnostics.Tracing;
using System.Collections.ObjectModel;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using AvaloniaGraphs.GraphControl;
using AvaloniaGraphs.GraphsLayout;
using WorldsFactory.world.events;
using Avalonia.Media;
using WorldsFactory.project;
using Avalonia.VisualTree;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.screen.panelCards;
using NP.Utilities;

namespace WorldsFactory.world.visualisations.events.relationGraphs.simpleRelationGraph;

public partial class SimpleEventRealtionGraph : UserControl
{
	private Graph graph = new Graph();
	public GraphView? graphView;
	private Dictionary<IEvent, GraphNode> eventToNode = new Dictionary<IEvent, GraphNode>();
	private StackPanel graphContainer;
	public SimpleEventRealtionGraph(IEvents events, IOverviewProjectActions overviewProjectActions)
	{
		this.InitializeComponent();
		graphContainer = this.FindControl<StackPanel>("GraphContianer")!;
		getNodes(events, overviewProjectActions);
		graph.Edges.AddAll(getEdges(events));
		graph.Layout = new SpringGraphLayout()
		{
			WithAnimation = true,
		};

		graphView = new GraphView(graph)
		{
			Width = 1000,
			Height = 800
		};
		graph.Layout?.ApplyLayout(graph);
		graphView.Background = new SolidColorBrush(Colors.DarkBlue);

		graphContainer.Children.Add(graphView);
	}

	protected override void OnAttachedToVisualTree(VisualTreeAttachmentEventArgs e)
	{
		base.OnAttachedToVisualTree(e);
	}

	private ObservableCollection<GraphEdge> getEdges(IEvents events)
	{
		var edges = new ObservableCollection<GraphEdge>();
		foreach (var ev in events.Collection)
		{
			foreach (var relatedEventRef in ev.SequenceManager.Events)
			{
				SetupNewEdge(edges, ev, relatedEventRef);
			}
		}
		return edges;
	}

	private void SetupNewEdge(ObservableCollection<GraphEdge> edges, IEvent ev, Reference<IEvent> relatedEventRef)
	{
		var relatedEvent = relatedEventRef.TryGetConcept();
		if (relatedEvent is null || relatedEvent is not IEvent)
			return;

		edges.Add(new GraphEdge(eventToNode[ev], eventToNode[(IEvent)relatedEvent])
		{
			IsDirected = true,
			ArrowHeadColor = new SolidColorBrush(Colors.Red),
			ArrowHeadLength = 10,
			ArrowHeadWidth = 10,
			Thickness = 2,
			Color = new SolidColorBrush(Colors.Red)
		});
	}

	private void getNodes(IEvents events, IOverviewProjectActions overviewProjectActions)
	{
		foreach (var ev in events.Collection)
		{
			SetupNewNode(overviewProjectActions, ev);
		}

		events.Collection.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (IEvent ev in args.NewItems)
				{
					SetupNewNode(overviewProjectActions, ev);
				}
			}
		};
	}

	private void SetupNewNode(IOverviewProjectActions overviewProjectActions, IEvent ev)
	{
		var width = 160;
		var height = 25;

		var node = new GraphNode()
		{
			Width = width,
			Height = height,
		};

		var textBlock = new TextBlock()
		{
			Text = ev.Name,
			FontSize = 14,
			Padding = new Thickness(5, 2, 5, 2),
			Width = width,
			Height = height,
			TextWrapping = TextWrapping.Wrap,
			TextAlignment = TextAlignment.Center,
		};

		textBlock.GetObservable(TextBox.TextProperty).Subscribe(text => 
		{
			if(text is not null){
				node.Width = text.Length * 10;
				node.Height = 25;
			}
		});
	
		node.ContentControl = new Border()
		{
			Child = textBlock,
			Background = new SolidColorBrush(Colors.Gray),
			BorderBrush = new SolidColorBrush(Colors.White),
			BorderThickness = new Thickness(1),
			Width = width,
			Height = height,
		};
		
		ev.OnIdChanged += (sender, args) =>
		{
			textBlock.Text = ev.Name;
		};
		
		ev.OnDelete += (sender, args) =>
		{
			graph.Nodes.Remove(eventToNode[ev]);
		};


		node.ContentControl.DoubleTapped += (sender, e) =>
		{
			if (sender is Control control)
			{
				var mainWindow = control.FindAncestorOfType<MainWindow>();
				if (mainWindow is not null)
				{
					overviewProjectActions.OnEventSelected(ev, PlaceToShowNewCard.NewPanelCardView);
				}
			}

		};

		ev.SequenceManager.Events.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (Reference<IEvent> relatedEventRef in args.NewItems)
				{
					SetupNewEdge(graph.Edges, ev, relatedEventRef);
				}
			}
			else if (args.OldItems is not null)
			{
				foreach (Reference<IEvent> relatedEventRef in args.OldItems)
				{
					var relatedEvent = relatedEventRef.TryGetConcept();
					if (relatedEvent is null || relatedEvent is not IEvent)
						continue;

					var relatedNode = eventToNode[ev];
					if (relatedNode is not null)
					{
						var edge = graph.Edges.FirstOrDefault(e => e.Start == relatedNode && e.End == eventToNode[(IEvent)relatedEvent]);
						if (edge is not null)
							graph.Edges.Remove(edge);
					}
						
				}
			}
		};

		eventToNode[ev] = node;
		graph.Nodes.Add(node);
	}
}