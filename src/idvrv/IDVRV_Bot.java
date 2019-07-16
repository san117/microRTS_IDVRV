package idvrv;

import ai.RandomBiasedAI;
import ai.abstraction.EMRDeterministico;
import ai.abstraction.SimpleEconomyRush;
import ai.abstraction.WorkerRushPlusPlus;
import ai.abstraction.cRush.CRush_V1;
import ai.abstraction.cRush.CRush_V2;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetNoPlan;
import ai.puppet.PuppetSearchAB;
import ai.puppet.SingleChoiceConfigurableScript;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author SANTIAGO & CLAUDIO
 */
public class IDVRV_Bot extends AIWithComputationBudget implements InterruptibleAI {

    //Puppet AI
    PuppetNoPlan strategyAI;
    NaiveMCTS tacticsAI;

    int weightStrategy,weightTactics;
    int origTimeBudget,origItBudget;

	//Constructor
    public IDVRV_Bot(UnitTypeTable utt) throws Exception{
        this(100,-1,20,80,new NaiveMCTS(100,-1,100,10,
			             0.3f, 0.0f, 0.4f,
			             new RandomBiasedAI(),
			             new CombinedEvaluation(), true));
        System.err.println(utt);
    }

	//Constructor
    public IDVRV_Bot(int mt, int mi, int weightStrategy, int weightTactics, NaiveMCTS tacticsAI) throws Exception {
        
        super(mt, mi);
        System.err.println("construction");
        origTimeBudget=mt;
        origItBudget=mi;
        this.strategyAI = null;
        this.tacticsAI=tacticsAI;
        this.weightStrategy=weightStrategy;
        this.weightTactics=weightTactics;
    }
    
    @Override
    public void reset() {
       
        System.err.println("reset: " + tacticsAI);
        if(strategyAI != null){
            System.err.println("reset no nulo: " + strategyAI);
            strategyAI.reset(); 
        }
        
        tacticsAI.reset();
    }

    @Override
	public void preGameAnalysis(GameState gs, long milliseconds) throws Exception
	{
    	 System.err.println("pre game analysis");
    	 this.strategyAI = new PuppetNoPlan(
    			 new PuppetSearchAB(100, -1,-1, -1, 100, 
    					 new SingleChoiceConfigurableScript (
    							 new FloodFillPathFinding(), getBestIA(gs,getMapWeight(gs))), new CombinedEvaluation()));
    	
    	
    	
	}
    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        
        System.err.println("getting action");

        
        if (gs.canExecuteAnyAction(player)) {
            startNewComputation(player, gs.clone());
            computeDuringOneGameFrame();
            return getBestActionSoFar();
        } else {
                return new PlayerAction();
        }
    }
    
    @Override
    public AI clone(){
        try{
            System.err.println("clonazepan!");
            return (AI)new IDVRV_Bot(
                        origTimeBudget, 
                        origItBudget, 
                        weightStrategy,
                        weightTactics,
                        //(PuppetNoPlan)strategyAI.clone(),
                        (NaiveMCTS)tacticsAI.clone());
            
        } catch(Exception e) {
            System.err.println("WHAT!");
            return null;
        }
    }
    
    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();
        return parameters;
    }
    
	//Get the weight of map based on the content.
    public int getMapWeight(GameState gs) throws Exception
    {
        System.err.println("getmapheight");
        PhysicalGameState pgs = gs.getPhysicalGameState();
        int height = pgs.getHeight();
        int widht = pgs.getWidth();
        int resources = 0;
        int bases = 0;

        for (Unit u2 : pgs.getUnits()) {

			//Resource founded!
            if (u2.getType().isResource) {
                resources++;
            }
            
			//Base founded!
            if (u2.getType().name.equals("Base")) {
                bases++;
            }
        }
        
		//Calculate Map Weight ("HashCode")
        return (height*widht) ^ (resources * bases);
    }
    
	//Based on map weight Find the best IA cabdidates
    private AI[] getBestIA(GameState gs, int mapWeight){
        
        System.err.println("getbestia");
        do{
            if(mapWeight > 4104)
                mapWeight = 4104;
            
            switch(mapWeight){
                case 68:
                    return new AI[]{
                            new POWorkerRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new CRush_V2(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 88:
                    return new AI[]{
                            new SimpleEconomyRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new PORangedRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 264:
                    return new AI[]{
                            new PORangedRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POHeavyRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 280:
                    return new AI[]{
                            new PORangedRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POHeavyRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 584:
                    return new AI[]{
                            new WorkerRushPlusPlus(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POWorkerRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new CRush_V1(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 704:
                    return new AI[]{
                            new POWorkerRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new EMRDeterministico(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new CRush_V2(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 1048:
                    return new AI[]{
                            new POWorkerRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POHeavyRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };
                case 4104:
                    return new AI[]{
                            new POWorkerRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new POLightRush(gs.getUnitTypeTable(), new AStarPathFinding()),
                            new CRush_V2(gs.getUnitTypeTable(), new AStarPathFinding()),
                    };          
            }
            
			//If the code not match, plus one, and repeat.
            mapWeight++;
        }while(true);
    }

    GameState _gs;
    
    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {

        System.err.println("entering startnewcomputation");
        
        _gs=gs.clone();
        ReducedGameState rgs=new ReducedGameState(_gs);

        assert(_gs.getTime()==rgs.getTime());
        //if(DEBUG>=1)System.err.println("Frame: "+gs.getTime()+" original size: "+gs.getUnits().size()+", reduced size: "+rgs.getUnits().size());
        boolean p0=false,p1=false;
        for(Unit u:rgs.getUnits()){
                if(u.getPlayer()==0)p0=true;
                if(u.getPlayer()==1)p1=true;
                if(p0&&p1)break;
        }

        if(!(p0&&p1) || !rgs.canExecuteAnyAction(player)){
                strategyAI.setTimeBudget(TIME_BUDGET);
                strategyAI.startNewComputation(player, _gs);
                tacticsAI.setTimeBudget(0);
        }else{
                strategyAI.setTimeBudget(TIME_BUDGET*weightStrategy/(weightStrategy+weightTactics));
                strategyAI.startNewComputation(player, _gs);

                tacticsAI.setTimeBudget(TIME_BUDGET*weightTactics/(weightStrategy+weightTactics));
                tacticsAI.startNewComputation(player, rgs);
        }    
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        System.err.println("computeduringongmeframe");
        strategyAI.computeDuringOneGameFrame();
        if(tacticsAI.getTimeBudget()>0){
            tacticsAI.computeDuringOneGameFrame();
        }    
    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        
        System.err.println("getbestctionsofar");
        
        if(tacticsAI.getTimeBudget()<=0){
            PlayerAction paStrategy=strategyAI.getBestActionSoFar();
            return paStrategy;
        }else{
            PlayerAction paStrategy=strategyAI.getBestActionSoFar();
            PlayerAction paTactics=tacticsAI.getBestActionSoFar();
            
            //remove non attacking units
            List<Pair<Unit,UnitAction>> toRemove=new ArrayList<Pair<Unit,UnitAction>>();
            for(Pair<Unit,UnitAction> ua:paTactics.getActions()) {
                if(!ua.m_a.getType().canAttack || 
                    ua.m_b.getType()==UnitAction.TYPE_PRODUCE ||
                    ua.m_b.getType()==UnitAction.TYPE_HARVEST ||
                    ua.m_b.getType()==UnitAction.TYPE_RETURN ){
                    toRemove.add(ua);
                }
            }
            
            for(Pair<Unit,UnitAction>ua:toRemove){
                //rgs.removeUnit(ua.m_a);
                paTactics.getActions().remove(ua);
            }

            PlayerAction paFull = new PlayerAction();
            //add extra actions
            List<Unit> skip=new ArrayList<Unit>();
            for(Pair<Unit,UnitAction> ua:paTactics.getActions()) {
                // check to see if the action is legal!
                PhysicalGameState pgs = _gs.getPhysicalGameState();
                ResourceUsage r = ua.m_b.resourceUsage(ua.m_a, pgs);
                boolean targetOccupied=false;
                for(int position:r.getPositionsUsed()) {
                    int y = position/pgs.getWidth();
                    int x = position%pgs.getWidth();
                    if (pgs.getTerrain(x, y) != PhysicalGameState.TERRAIN_NONE ||
                        pgs.getUnitAt(x, y) != null) {
                        targetOccupied=true;
                        break;
                    }
                }
                
                if(!targetOccupied && r.consistentWith(paStrategy.getResourceUsage(), _gs)){
                        paFull.addUnitAction(ua.m_a, ua.m_b);
                        paFull.getResourceUsage().merge(r);
                        skip.add(ua.m_a);
                }
            }

            //add script actions
            for(Pair<Unit,UnitAction> ua:paStrategy.getActions()) {
                    boolean found=false;
                    for(Unit u:skip){
                            if(u.getID()==ua.m_a.getID()){
                                    found=true;
                                    break;
                            }
                    }
                    if(found){//skip units that were assigned by the extra AI
                        continue;
                    }
                    paFull.addUnitAction(ua.m_a, ua.m_b);
                    paFull.getResourceUsage().merge(ua.m_b.resourceUsage(ua.m_a, _gs.getPhysicalGameState()));
            }
            return paFull;
        }
    }
}
