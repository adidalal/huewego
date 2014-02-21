#include <pebble.h>
 
Window *window;
TextLayer *text_layer_1, *text_layer_2;
char tap_text[3];
static AppTimer *timer;
static TextLayer *text_layer;
static BitmapLayer *bm_layer;
static GBitmap *image;
float prevvelx,prevvely,prevvelz;

 
void accel_handler(AccelData *data, uint32_t num_samples)
{

  if ((data[0].x > 350) || (data[0].y > 150) || (data[0].z > 450)){
	//text_layer_set_text(text_layer_1, "YOU LOST - POSITIVE");
	bitmap_layer_set_bitmap(bm_layer, gbitmap_create_with_resource(RESOURCE_ID_failure));
	  APP_LOG(APP_LOG_LEVEL_DEBUG, "Got acc %d", data[0].x);
	}
  if ((data[0].x < -350) || (data[0].y < -650) || (data[0].z < -1350)){
	//text_layer_set_text(text_layer_1, "YOU LOST - NEGATIVE");
	  bitmap_layer_set_bitmap(bm_layer, gbitmap_create_with_resource(RESOURCE_ID_failure));
	APP_LOG(APP_LOG_LEVEL_DEBUG, "Got acc %d %d %d", data[0].x,data[0].y,data[0].z);
  }
    
}
 
void window_load(Window *window)
{
  Layer *window_layer = window_get_root_layer(window);
  image = gbitmap_create_with_resource(RESOURCE_ID_static);
  bm_layer = bitmap_layer_create(GRect(0,0,144,168));
  bitmap_layer_set_bitmap(bm_layer, image);
	  layer_add_child(window_layer, (Layer*) bm_layer);
 /*
  text_layer_1 = text_layer_create(GRect(0, 0, 144, 20));
  layer_add_child(window_layer, text_layer_get_layer(text_layer_1));
 
  text_layer_2 = text_layer_create(GRect(0, 20, 144, 20));
  layer_add_child(window_layer, text_layer_get_layer(text_layer_2));
 */
  //accel_data_service_subscribe(10, accel_handler);
  accel_service_set_sampling_rate(ACCEL_SAMPLING_25HZ);
 
  //accel_tap_service_subscribe(tap_handler);
}
 
void window_unload(Window *window)
{
  // Call this before destroying text_layer, because it can change the text
  // and this must only happen while the layer exists.
  accel_data_service_unsubscribe();
  accel_tap_service_unsubscribe();
 
  text_layer_destroy(text_layer_2);
  text_layer_destroy(text_layer_1);
  bitmap_layer_destroy(bm_layer);
}

static void timer_callback(void *data) {
  text_layer_set_text(text_layer, "Timer happened!");
}

#define KEY_COLOR 1
#define COLOR_GREEN 0
#define COLOR_RED 1
	
void inbox_received(DictionaryIterator *received, void *context) {
  APP_LOG(APP_LOG_LEVEL_DEBUG, "Received message!");
	
  Tuple *tuple = dict_find(received, KEY_COLOR);
	if (tuple) {
		int color = tuple->value->int32;
		APP_LOG(APP_LOG_LEVEL_DEBUG, "Got color %d", color);
		if (color == COLOR_RED) 
			  accel_data_service_subscribe(25, accel_handler);
		if (color == 1337)
			//1337 is win
			bitmap_layer_set_bitmap(bm_layer, gbitmap_create_with_resource(RESOURCE_ID_walrus_of_win));
	}
	
}

int main()
{
  APP_LOG(APP_LOG_LEVEL_DEBUG, "Starting!");
  window = window_create();
  window_set_window_handlers(window, (WindowHandlers)
  {
    .load = window_load,
    .unload = window_unload,
  });
  window_stack_push(window, true);
	
  app_message_register_inbox_received(inbox_received);
  app_message_open(64, 64);
  //timer = app_timer_register(100 /* milliseconds */, timer_callback, NULL);
  app_event_loop();
  window_destroy(window);
}