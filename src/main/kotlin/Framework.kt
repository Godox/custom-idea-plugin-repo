import kotlin.reflect.KProperty

class PointerBinding<T : Any> {

    internal lateinit var value: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

}