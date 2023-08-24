package net.ranzer.grimco.pillbot.functions.pillReminder;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PillReminderManager {

	ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

	public void schedulePillReminder(PillReminder pr){

		var pillReminderFuture = ses.scheduleAtFixedRate(
				pr::makePing,
		        pr.START_DATE.getTime()-System.currentTimeMillis(),
				TimeUnit.HOURS.toMillis(pr.INTERVAL_IN_HOURS),
				TimeUnit.MILLISECONDS);

		pr.setTimerEvent(pillReminderFuture);
	}

	public void cancelPillReminder(PillReminder pr){
		pr.getTimerEvent().cancel(true);
	}

	public static class PillReminder{
		private Member user;
		private TextChannel reminderChannel;
		private String pill;
		private String dose;

		final Date START_DATE;
		final int INTERVAL_IN_HOURS;

		private ScheduledFuture<?> timerEvent;

		public PillReminder(Date startDate, int intervalInHours){
			this.START_DATE = startDate;
			this.INTERVAL_IN_HOURS = intervalInHours;
		}

		public void makePing(){
			String formatStringWithDose = "%s it's time for your %s dose of %s%s";

			reminderChannel.sendMessage(
					String.format(formatStringWithDose,
					              user.getAsMention(),
					              DateFormat.getTimeInstance(DateFormat.LONG).format(START_DATE),
					              dose!=null?pill:pill+": "+dose
					              )).queue();
		}

		public void setTimerEvent(ScheduledFuture<?> timerEvent) {
			this.timerEvent = timerEvent;
		}

		public ScheduledFuture<?> getTimerEvent() {
			return timerEvent;
		}
	}

}
