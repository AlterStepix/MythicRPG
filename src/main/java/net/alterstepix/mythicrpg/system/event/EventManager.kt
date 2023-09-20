package net.alterstepix.mythicrpg.system.event

object EventManager {
    private val listeners = mutableListOf<(MEvent) -> Unit>()

    fun addListener(lambda: (MEvent) -> Unit) {
        this.listeners.add(lambda)
    }

    inline fun <reified T> register(crossinline lambda: (T) -> Unit) {
        addListener(lambda = { event ->
            if(event is T) { lambda(event) }
        })
    }

    fun launch(event: MEvent) {
        for(listener in listeners) {
            listener(event)
        }
    }
}