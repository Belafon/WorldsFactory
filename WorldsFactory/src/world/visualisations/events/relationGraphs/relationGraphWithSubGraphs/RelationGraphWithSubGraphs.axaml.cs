using System.Collections.ObjectModel;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Data;
using Avalonia.Markup.Xaml;
using Avalonia.Media;
using Avalonia.VisualTree;
using AvaloniaEdit.Rendering;
using AvaloniaGraphs.GraphControl;
using AvaloniaGraphs.GraphsLayout;
using NP.Utilities;
using WorldsFactory.project;
using WorldsFactory.world.events;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.visualisations.events.relationGraphs.relationGraphWithSubGraphs;

/// <summary>
/// Diaplays a graph of relations between events.
/// The subgraphs are the event containers.
/// </summary>
public partial class RelationGraphWithSubGraphs : UserControl
{
	private Graph graph = new Graph();
	public GraphView? graphView;
	private Dictionary<IEvent, GraphNode> eventToNode = new Dictionary<IEvent, GraphNode>();
	private Grid graphContainer;
	
	/// <summary>
	/// Diaplays a graph of relations between events.
	/// The subgraphs are the event containers.
	/// </summary>
	public RelationGraphWithSubGraphs(IEvents events, IOverviewProjectActions overviewProjectActions)
	{
		this.InitializeComponent();
		graphContainer = this.FindControl<Grid>("GraphContianer")!;
		getNodes(events, overviewProjectActions);
		graph.Edges.AddAll(getEdges(events));
		graph.Layout = new SpringGraphLayoutWithSubGraphs
		{
			WithAnimation = false,
		};

		graphView = new GraphView(graph)
		{
			Width = double.NaN,
			Height = double.NaN,
			HorizontalAlignment = Avalonia.Layout.HorizontalAlignment.Stretch,
			VerticalAlignment = Avalonia.Layout.VerticalAlignment.Stretch
		};

		graph.Layout?.ApplyLayout(graph);
		graphView.Background = new SolidColorBrush(Colors.DarkBlue);

		Grid.SetRow(graphView, 0);
		Grid.SetColumn(graphView, 0);
		graphView.HorizontalAlignment = Avalonia.Layout.HorizontalAlignment.Stretch;
		graphView.VerticalAlignment = Avalonia.Layout.VerticalAlignment.Stretch;
		graphContainer.Children.Add(graphView);

	}

	private ObservableCollection<GraphEdge> getEdges(IEvents events)
	{
		var edges = new ObservableCollection<GraphEdge>();
		foreach (var ev in events.Collection)
		{
			foreach (var relatedEventRef in ev.SequenceManager.Events)
			{
				setupNewEdge(edges, ev, relatedEventRef);
			}
		}
		return edges;
	}

	private void setupNewEdge(ObservableCollection<GraphEdge> edges, IEvent ev, Reference<IEvent> relatedEventRef)
	{
		var relatedEvent = relatedEventRef.TryGetConcept();
		if (relatedEvent is null || relatedEvent is not IEvent)
			return;

		if (!eventToNode.ContainsKey(ev))
			return;  // TODO avoid this

		edges.Add(new GraphEdge(eventToNode[(IEvent)relatedEvent], eventToNode[ev])
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
		// setup events nodes in first level first
		foreach (var ev in events.Collection)
		{
			if (ev.SequenceManager.EventContainer is null)
				SetupNewNode(overviewProjectActions, ev);
		}

		// setup nodes for subgraphs in first level
		foreach (var container in events.FirstLevelEventContainers)
		{
			var subGraph = setupNewSubGraph(container, overviewProjectActions);
			graph.Nodes.Add(subGraph);
		}

		events.Collection.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (IEvent ev in args.NewItems)
				{
					if (ev.SequenceManager.EventContainer is null)
						SetupNewNode(overviewProjectActions, ev);
					else
					{
						var subGraph = setupNewSubGraph(ev.SequenceManager.EventContainer, overviewProjectActions);
						graph.Nodes.Add(subGraph);
					}
				}
			}
		};
	}

	private SubGraph setupNewSubGraph(EventContainer container, IOverviewProjectActions overviewProjectActions)
	{
		var subGraphNodes = new ObservableCollection<GraphNode>();
		var subGraphEdges = new ObservableCollection<GraphEdge>();

		foreach (var evRef in container.Events)
		{
			var ev = (IEvent)evRef.TryGetConcept()!;
			if (ev is null)
				continue;
			var node = createNewNode(overviewProjectActions, ev);
			subGraphNodes.Add(node);
			eventToNode[ev] = node;
		}

		foreach (var subContainerRef in container.SubContainers)
		{
			var subContainer = (EventContainer)subContainerRef.TryGetConcept()!;
			if (subContainer is null)
				continue;
			var subsubGraph = setupNewSubGraph(subContainer, overviewProjectActions);
			subGraphNodes.Add(subsubGraph);
		}

		var subGraph = createSubGraphNode(container, subGraphNodes, subGraphEdges);

		return subGraph;

	}

	private static SubGraph createSubGraphNode(EventContainer container, ObservableCollection<GraphNode> subGraphNodes, ObservableCollection<GraphEdge> subGraphEdges)
	{
		var subGraph = new SubGraph();
		subGraph.BorderContainerTitle.Text = container.Name;
		subGraph.BorderContainerTitle.Padding = new Thickness(10, 4, 10, 4);

		subGraph.Graph.Nodes.AddAll(subGraphNodes);
		subGraph.Graph.Edges.AddAll(subGraphEdges);
		subGraph.Graph.Layout = new SpringGraphLayoutWithSubGraphs
		{
			WithAnimation = true,
		};

		var containersTitle = new TextBlock()
		{
			Text = container.Name,
			FontSize = 14,
			Padding = new Thickness(10, 4, 10, 4),
			TextWrapping = TextWrapping.Wrap,
			TextAlignment = TextAlignment.Center,
			Background = new SolidColorBrush(Colors.DarkViolet),
		};

		var subGraphControl = new Border()
		{
			Child = containersTitle,
			BorderBrush = new SolidColorBrush(Colors.White),
			BorderThickness = new Thickness(1),
		};


		containersTitle.GetObservable(TextBox.TextProperty).Subscribe(text =>
		{
			if (text is not null)
			{
				subGraph.Width = text.Length * 8 + 10;
				subGraph.Height = 28;
				subGraphControl.Width = text.Length * 8 + 10;
				subGraphControl.Height = 28;
				containersTitle.Width = text.Length * 8 + 10;
				containersTitle.Height = 28;
			}
		});


		subGraph.ContentContainer.Children.Add(subGraphControl);

		return subGraph;
	}

	private void SetupNewNode(IOverviewProjectActions overviewProjectActions, IEvent ev)
	{
		GraphNode node = createNewNode(overviewProjectActions, ev);

		eventToNode[ev] = node;
		graph.Nodes.Add(node);
	}

	private GraphNode createNewNode(IOverviewProjectActions overviewProjectActions, IEvent ev)
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

		node.ContentControl = new Border()
		{
			Child = textBlock,
			Background = new SolidColorBrush(Colors.Gray),
			BorderBrush = new SolidColorBrush(Colors.White),
			BorderThickness = new Thickness(1),
			Width = width,
			Height = height,
		};

		textBlock.GetObservable(TextBox.TextProperty).Subscribe(text =>
		{
			if (text is not null)
			{
				node.Width = text.Length * 10 + 10;
				node.Height = 25;
				node.ContentControl.Width = text.Length * 10 + 10;
				node.ContentControl.Height = 25;
				textBlock.Width = text.Length * 10 + 10;
				textBlock.Height = 25;
			}
		});


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
					setupNewEdge(graph.Edges, ev, relatedEventRef);
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
		return node;
	}
}