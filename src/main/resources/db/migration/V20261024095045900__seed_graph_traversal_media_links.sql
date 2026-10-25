-- Flyway migration V20261024095045900: Seed concept media links for graph traversal and instant search
INSERT INTO concept_media_links (canon_item_id, media_type, podcast_episode_id, relationship_type, description)
SELECT c.id, 'PODCAST', p.id, 'DISCUSSES', 'Podcast episode discussing extended mind concept'
FROM canon_items c, podcast_episodes p
WHERE c.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND p.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.podcast_episode_id = p.id
  );

INSERT INTO concept_media_links (canon_item_id, media_type, essay_id, relationship_type, description)
SELECT c.id, 'ESSAY', e.id, 'ELABORATES', 'Essay elaborating on extended mind concept'
FROM canon_items c, essays e
WHERE c.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND e.slug = 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy'
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.essay_id = e.id
  );

INSERT INTO concept_media_links (canon_item_id, media_type, spark_id, relationship_type, description)
SELECT c.id, 'SPARK', s.id, 'CAPTURES', 'Spark capturing core extended mind insight'
FROM canon_items c, sparks s
WHERE c.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND s.telegram_message_id = 8001
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.spark_id = s.id
  );

INSERT INTO concept_media_links (canon_item_id, media_type, podcast_episode_id, relationship_type, description)
SELECT c.id, 'PODCAST', p.id, 'DISCUSSES', 'Podcast episode discussing deontic autonomy of agents'
FROM canon_items c, podcast_episodes p
WHERE c.slug = 'deonticheskaya-avtonomiya-agentov'
  AND p.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.podcast_episode_id = p.id
  );
