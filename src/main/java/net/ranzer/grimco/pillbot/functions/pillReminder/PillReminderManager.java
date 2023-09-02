package net.ranzer.grimco.pillbot.functions.pillReminder;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PillReminderManager {

	private final static PillReminderManager instance = new PillReminderManager();

	public static PillReminderManager getInstance(){
		return instance;
	}

	private PillReminderManager(){
		//todo load state from persistent file on load
	}
	ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

	private Map<UUID,PillReminder> reminders = new HashMap<>();

	public PillReminder schedulePillReminder(PillReminder pr){

		long timeTillFirstRun = ZonedDateTime.now().until(pr.TIME,ChronoUnit.MILLIS);
		if (timeTillFirstRun<0){
			timeTillFirstRun += 24*60*60*1000;
		}

		System.out.println(timeTillFirstRun);

		var pillReminderFuture = ses.scheduleAtFixedRate(
				pr::makePing,
		        timeTillFirstRun,
				24*60*60*1000,//24 hours in millis
				TimeUnit.MILLISECONDS);
		System.out.println(pillReminderFuture.getDelay(TimeUnit.MILLISECONDS));

		System.out.println(pillReminderFuture);
		pr.setTimerEvent(pillReminderFuture);
		reminders.put(pr.id,pr);
		return pr;
	}

	public PillReminder schedulePillReminder(String name, ZonedDateTime time, MessageChannel channel, String dose,Member user){
		PillReminder rtn = new PillReminder(time,name,channel,user,dose);

		return schedulePillReminder(rtn);
	}

	public void cancelPillReminder(PillReminder pr){
		pr.getTimerEvent().cancel(true);
		reminders.remove(pr.id);
	}

	public void cancelPillReminder(String UUID){
		cancelPillReminder(reminders.get(java.util.UUID.fromString(UUID)));
	}

	public List<PillReminder> getPills(Member member) {
		return reminders.values().stream().filter(pr -> pr.user.equals(member)).collect(Collectors.toList());
	}

	public static class PillReminder{
		public final UUID id = UUID.randomUUID();
		private final Member user;
		private final MessageChannel reminderChannel;
		public final String name;
		private final String dose;

		public final ZonedDateTime TIME;

		private ScheduledFuture<?> timerEvent;

		public PillReminder(ZonedDateTime startTime, String name, MessageChannel channel, Member user,String dose){
			this.TIME            = startTime;
			this.name            = name;
			this.reminderChannel = channel;
			this.user            = user;
			this.dose            = dose;
		}

		public void makePing(){
			String formatStringWithDose = "%s it's time for your %s dose of %s";

			System.out.println("trying to run...");

			reminderChannel.sendMessage(
					String.format(formatStringWithDose,
					              user.getAsMention(),
					              TIME.toLocalTime().toString(),
					              dose!=null? name : name + ": " + dose
					              )).queue();
		}

		public void setTimerEvent(ScheduledFuture<?> timerEvent) {
			this.timerEvent = timerEvent;
		}

		public ScheduledFuture<?> getTimerEvent() {
			return timerEvent;
		}

		@Override
		public String toString() {
			return String.format("Pill: %s\nReminder Time: %s\nID for removal: %s",
			                     name,
			                     TIME.toLocalTime() + " " + TIME.getZone().toString(),
			                     id);
		}
	}

}
