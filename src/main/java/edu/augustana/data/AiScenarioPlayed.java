package edu.augustana.data;

import edu.augustana.dataModel.AiScenarioData;
import edu.augustana.helper.handler.GeminiAiHandler;

public class AiScenarioPlayed {
    public static AiScenarioPlayed instance = new AiScenarioPlayed();
        public AiScenarioData data;
        public GeminiAiHandler AIHandler = new GeminiAiHandler();
        public boolean isInit = false;
        public AiScenarioPlayed(){

        }
        public void setData(AiScenarioData data){
            this.isInit = true;
            this.data = data;
        }
        public AiScenarioData getData(){
            return this.data;
        }
        public void clearData(){
            this.isInit = false;
            this.data = null;
            this.AIHandler = new GeminiAiHandler();
        }

}
