import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;


public class ApplicationStartGUI extends Application {
	//private Stage stage; //removed - CJL

	@Override
	public void start(Stage primaryStage) {
		//removed stage variable, it isnt needed, it is a duplicate variable - CJL
		//stage = primaryStage; - CJL
		try {
		    ElevatorController ctrl = new ElevatorController();
		    ctrl.setStage(primaryStage);

            Scene scene = new Scene(ctrl, ctrl.WINDOW_WIDTH, ctrl.WINDOW_HEIGHT);
            primaryStage.setTitle("DS et AL Elevator Project");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                   Platform.exit();
                   System.exit(0);
                }
             });
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
