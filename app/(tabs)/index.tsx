import React, { useState, useMemo } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Modal,
  Platform,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";
import { Ionicons, MaterialCommunityIcons, Feather } from "@expo/vector-icons";
import { trpc } from "@/lib/trpc";
import { useAuth } from "@/hooks/use-auth";

interface VoiceRecordItem {
  id: number;
  userOpenId: string;
  title: string;
  category: string;
  status: "completed" | "processing" | "draft" | "failed";
  modelName: string | null;
  voiceName: string | null;
  durationSeconds: number;
  fileSize: string | null;
  audioUrl: string | null;
  transcript: string | null;
  tags: string | null;
  createdAt: string | Date;
}

export default function DashboardScreen() {
  const { user } = useAuth();
  const [selectedCategory, setSelectedCategory] = useState<string>("All");
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [isPlayingId, setIsPlayingId] = useState<number | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [newTitle, setNewTitle] = useState("");
  const [newCategory, setNewCategory] = useState("Voice Clone");
  const [newVoiceName, setNewVoiceName] = useState("Aura Studio Pro (Warm Neutral)");
  const [newTranscript, setNewTranscript] = useState("");

  const utils = trpc.useUtils();

  // Fetch user-specific records via TRPC
  const recordsQuery = trpc.records.list.useQuery({
    category: selectedCategory,
    search: searchQuery,
  });

  // Fetch overview stats via TRPC
  const statsQuery = trpc.records.getStats.useQuery();
  const dashboardQuery = trpc.dashboard.getOverview.useQuery();

  // Mutations
  const createRecordMutation = trpc.records.create.useMutation({
    onSuccess: () => {
      utils.records.list.invalidate();
      utils.records.getStats.invalidate();
      utils.dashboard.getOverview.invalidate();
      setIsCreateModalOpen(false);
      setNewTitle("");
      setNewTranscript("");
      Alert.alert("Sucesso", "Novo projeto de voz criado no estúdio!");
    },
    onError: (err) => {
      Alert.alert("Erro", err.message || "Falha ao criar gravação.");
    },
  });

  const deleteRecordMutation = trpc.records.delete.useMutation({
    onSuccess: () => {
      utils.records.list.invalidate();
      utils.records.getStats.invalidate();
      utils.dashboard.getOverview.invalidate();
    },
  });

  const categories = ["All", "Voice Clone", "Audio Enhancement", "Text-to-Speech"];

  const handleCreate = () => {
    if (!newTitle.trim()) {
      Alert.alert("Aviso", "Por favor, insira o título do projeto.");
      return;
    }
    createRecordMutation.mutate({
      title: newTitle.trim(),
      category: newCategory,
      voiceName: newVoiceName,
      transcript: newTranscript.trim() || undefined,
      durationSeconds: Math.floor(Math.random() * 120) + 30,
    });
  };

  const handleDelete = (id: number, title: string) => {
    Alert.alert("Excluir Gravação", `Deseja realmente excluir "${title}"?`, [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Excluir",
        style: "destructive",
        onPress: () => deleteRecordMutation.mutate({ id }),
      },
    ]);
  };

  const togglePlayback = (id: number) => {
    if (isPlayingId === id) {
      setIsPlayingId(null);
    } else {
      setIsPlayingId(id);
    }
  };

  const records = (recordsQuery.data as unknown as VoiceRecordItem[]) || [];
  const stats = statsQuery.data || {
    totalProjects: 0,
    completedCount: 0,
    processingCount: 0,
    totalAudioMinutes: 0,
    voiceCreditsRemaining: 850,
    voiceCreditsTotal: 1000,
    activeModelsCount: 4,
  };

  const isRefreshing = recordsQuery.isRefetching || statsQuery.isRefetching;

  const onRefresh = () => {
    recordsQuery.refetch();
    statsQuery.refetch();
    dashboardQuery.refetch();
  };

  return (
    <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
      <StatusBar style="light" backgroundColor="#0b0b12" />

      {/* Header Bar */}
      <View style={styles.header}>
        <View style={styles.headerUser}>
          <View style={styles.avatar}>
            <MaterialCommunityIcons name="waveform" size={24} color="#bc5cff" />
          </View>
          <View>
            <Text style={styles.greeting}>Vozo Voice Dashboard</Text>
            <Text style={styles.userName}>
              {user?.name || dashboardQuery.data?.user?.name || "Creator Studio"}
              <Text style={styles.userBadge}> • PRO</Text>
            </Text>
          </View>
        </View>

        <TouchableOpacity
          style={styles.createButton}
          onPress={() => setIsCreateModalOpen(true)}
          activeOpacity={0.8}
        >
          <Feather name="plus" size={18} color="#170c22" />
          <Text style={styles.createButtonText}>Criar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView
        showsVerticalScrollIndicator={false}
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl refreshing={isRefreshing} onRefresh={onRefresh} tintColor="#bc5cff" />
        }
      >
        {/* Analytics & Metrics Cards */}
        <View style={styles.metricsGrid}>
          <View style={[styles.metricCard, { borderColor: "#2e1c4b" }]}>
            <View style={styles.metricHeader}>
              <Text style={styles.metricLabel}>Total de Projetos</Text>
              <Ionicons name="folder-outline" size={18} color="#bc5cff" />
            </View>
            <Text style={styles.metricValue}>{stats.totalProjects}</Text>
            <Text style={styles.metricSub}>
              {stats.completedCount} concluídos • {stats.processingCount} em fila
            </Text>
          </View>

          <View style={[styles.metricCard, { borderColor: "#2e1c4b" }]}>
            <View style={styles.metricHeader}>
              <Text style={styles.metricLabel}>Minutos de Áudio</Text>
              <Ionicons name="mic-outline" size={18} color="#10b981" />
            </View>
            <Text style={styles.metricValue}>{stats.totalAudioMinutes}m</Text>
            <Text style={styles.metricSub}>Síntese neural de alta fidelidade</Text>
          </View>
        </View>

        {/* Credit Usage Progress Card */}
        <View style={styles.creditsCard}>
          <View style={styles.creditsHeader}>
            <View style={{ flexDirection: "row", alignItems: "center", gap: 8 }}>
              <MaterialCommunityIcons name="lightning-bolt" size={20} color="#f59e0b" />
              <Text style={styles.creditsTitle}>Créditos de Voz Disponíveis</Text>
            </View>
            <Text style={styles.creditsValue}>
              {stats.voiceCreditsRemaining} / {stats.voiceCreditsTotal}
            </Text>
          </View>
          <View style={styles.progressBarBackground}>
            <View
              style={[
                styles.progressBarFill,
                { width: `${(stats.voiceCreditsRemaining / stats.voiceCreditsTotal) * 100}%` },
              ]}
            />
          </View>
          <Text style={styles.creditsFooter}>
            Modelos ativos: {stats.activeModelsCount} • Renovação em 14 dias
          </Text>
        </View>

        {/* Search & Category Filter Section */}
        <View style={styles.filterSection}>
          <View style={styles.searchBar}>
            <Feather name="search" size={18} color="#aaa2b8" />
            <TextInput
              style={styles.searchInput}
              placeholder="Buscar gravações, vozes ou transcrições..."
              placeholderTextColor="#776c8c"
              value={searchQuery}
              onChangeText={setSearchQuery}
            />
            {searchQuery.length > 0 && (
              <TouchableOpacity onPress={() => setSearchQuery("")}>
                <Ionicons name="close-circle" size={18} color="#aaa2b8" />
              </TouchableOpacity>
            )}
          </View>

          {/* Category Tabs */}
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.categoryScroll}>
            {categories.map((cat) => {
              const isSelected = selectedCategory === cat;
              return (
                <TouchableOpacity
                  key={cat}
                  onPress={() => setSelectedCategory(cat)}
                  style={[styles.categoryChip, isSelected && styles.categoryChipActive]}
                >
                  <Text
                    style={[styles.categoryChipText, isSelected && styles.categoryChipTextActive]}
                  >
                    {cat}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </ScrollView>
        </View>

        {/* Records List Header */}
        <View style={styles.recordsListHeader}>
          <Text style={styles.sectionTitle}>Gravações do Usuário (TRPC)</Text>
          <Text style={styles.recordCount}>{records.length} itens encontrados</Text>
        </View>

        {/* Records List or Empty State */}
        {recordsQuery.isLoading ? (
          <View style={styles.loadingContainer}>
            <ActivityIndicator size="large" color="#bc5cff" />
            <Text style={styles.loadingText}>Carregando registros de voz...</Text>
          </View>
        ) : records.length === 0 ? (
          <View style={styles.emptyContainer}>
            <MaterialCommunityIcons name="microphone-off" size={48} color="#4c3d66" />
            <Text style={styles.emptyTitle}>Nenhuma gravação encontrada</Text>
            <Text style={styles.emptySubtitle}>
              Crie seu primeiro projeto de clonagem de voz ou síntese vocal para começar.
            </Text>
            <TouchableOpacity
              style={[styles.createButton, { marginTop: 16 }]}
              onPress={() => setIsCreateModalOpen(true)}
            >
              <Feather name="plus" size={16} color="#170c22" />
              <Text style={styles.createButtonText}>Criar Primeiro Projeto</Text>
            </TouchableOpacity>
          </View>
        ) : (
          records.map((item) => (
            <View key={item.id} style={styles.recordCard}>
              <View style={styles.recordMain}>
                <View style={styles.recordInfo}>
                  <View style={styles.tagRow}>
                    <View
                      style={[
                        styles.statusBadge,
                        item.status === "completed"
                          ? styles.statusCompleted
                          : item.status === "processing"
                          ? styles.statusProcessing
                          : styles.statusFailed,
                      ]}
                    >
                      <Text style={styles.statusText}>
                        {item.status === "completed"
                          ? "Concluído"
                          : item.status === "processing"
                          ? "Processando"
                          : "Pendente"}
                      </Text>
                    </View>
                    <Text style={styles.categoryTag}>{item.category}</Text>
                    {item.fileSize && <Text style={styles.sizeTag}>{item.fileSize}</Text>}
                  </View>

                  <Text style={styles.recordTitle}>{item.title}</Text>

                  <View style={styles.metaRow}>
                    <Ionicons name="person-outline" size={13} color="#aaa2b8" />
                    <Text style={styles.recordMeta}>
                      {item.voiceName || "Voz Padrão"} ({item.modelName || "Vozo AI"})
                    </Text>
                    <Text style={styles.dot}>•</Text>
                    <Ionicons name="time-outline" size={13} color="#aaa2b8" />
                    <Text style={styles.recordMeta}>{item.durationSeconds}s</Text>
                  </View>

                  {item.transcript && (
                    <Text style={styles.transcriptPreview} numberOfLines={2}>
                      "{item.transcript}"
                    </Text>
                  )}
                </View>
              </View>

              {/* Action Buttons */}
              <View style={styles.recordActions}>
                <TouchableOpacity
                  style={[
                    styles.playButton,
                    isPlayingId === item.id && styles.playButtonActive,
                  ]}
                  onPress={() => togglePlayback(item.id)}
                >
                  <Ionicons
                    name={isPlayingId === item.id ? "pause" : "play"}
                    size={16}
                    color={isPlayingId === item.id ? "#170c22" : "#bc5cff"}
                  />
                  <Text
                    style={[
                      styles.playButtonText,
                      isPlayingId === item.id && styles.playButtonTextActive,
                    ]}
                  >
                    {isPlayingId === item.id ? "Pausar" : "Ouvir"}
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={styles.deleteButton}
                  onPress={() => handleDelete(item.id, item.title)}
                >
                  <Feather name="trash-2" size={16} color="#ef4444" />
                </TouchableOpacity>
              </View>
            </View>
          ))
        )}

        <View style={{ height: 40 }} />
      </ScrollView>

      {/* Modal to Create New Voice Record */}
      <Modal
        visible={isCreateModalOpen}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setIsCreateModalOpen(false)}
      >
        <View style={styles.modalBackdrop}>
          <View style={styles.modalCard}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Novo Projeto de Voz</Text>
              <TouchableOpacity onPress={() => setIsCreateModalOpen(false)}>
                <Ionicons name="close" size={24} color="#aaa2b8" />
              </TouchableOpacity>
            </View>

            <Text style={styles.inputLabel}>Título da Gravação / Projeto</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Introdução Podcast Episódio 5"
              placeholderTextColor="#776c8c"
              value={newTitle}
              onChangeText={setNewTitle}
            />

            <Text style={styles.inputLabel}>Categoria</Text>
            <View style={styles.modalPills}>
              {["Voice Clone", "Audio Enhancement", "Text-to-Speech"].map((cat) => (
                <TouchableOpacity
                  key={cat}
                  style={[styles.modalPill, newCategory === cat && styles.modalPillActive]}
                  onPress={() => setNewCategory(cat)}
                >
                  <Text
                    style={[
                      styles.modalPillText,
                      newCategory === cat && styles.modalPillTextActive,
                    ]}
                  >
                    {cat}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>

            <Text style={styles.inputLabel}>Modelo / Perfil de Voz</Text>
            <TextInput
              style={styles.modalInput}
              placeholder="Ex: Aura Studio Pro (Warm Neutral)"
              placeholderTextColor="#776c8c"
              value={newVoiceName}
              onChangeText={setNewVoiceName}
            />

            <Text style={styles.inputLabel}>Texto / Transcrição (Opcional)</Text>
            <TextInput
              style={[styles.modalInput, { height: 75, textAlignVertical: "top" }]}
              placeholder="Insira o roteiro ou texto da síntese vocal..."
              placeholderTextColor="#776c8c"
              multiline
              numberOfLines={3}
              value={newTranscript}
              onChangeText={setNewTranscript}
            />

            <View style={styles.modalFooter}>
              <TouchableOpacity
                style={styles.cancelButton}
                onPress={() => setIsCreateModalOpen(false)}
              >
                <Text style={styles.cancelButtonText}>Cancelar</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.confirmButton}
                onPress={handleCreate}
                disabled={createRecordMutation.isPending}
              >
                {createRecordMutation.isPending ? (
                  <ActivityIndicator size="small" color="#170c22" />
                ) : (
                  <Text style={styles.confirmButtonText}>Gerar Registro</Text>
                )}
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#0b0b12",
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 18,
    paddingVertical: 14,
    borderBottomWidth: 1,
    borderBottomColor: "#1d122e",
    backgroundColor: "#0b0b12",
  },
  headerUser: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },
  avatar: {
    width: 42,
    height: 42,
    borderRadius: 12,
    backgroundColor: "#1c102c",
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: "#371c5a",
  },
  greeting: {
    fontSize: 12,
    color: "#aaa2b8",
    fontWeight: "500",
  },
  userName: {
    fontSize: 16,
    color: "#ffffff",
    fontWeight: "700",
  },
  userBadge: {
    color: "#bc5cff",
    fontSize: 12,
    fontWeight: "800",
  },
  createButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    backgroundColor: "#bc5cff",
    paddingHorizontal: 16,
    paddingVertical: 9,
    borderRadius: 20,
  },
  createButtonText: {
    color: "#170c22",
    fontWeight: "700",
    fontSize: 13,
  },
  scrollContent: {
    padding: 16,
  },
  metricsGrid: {
    flexDirection: "row",
    gap: 12,
    marginBottom: 14,
  },
  metricCard: {
    flex: 1,
    backgroundColor: "#160c24",
    borderRadius: 16,
    padding: 14,
    borderWidth: 1,
  },
  metricHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 6,
  },
  metricLabel: {
    fontSize: 12,
    color: "#aaa2b8",
    fontWeight: "500",
  },
  metricValue: {
    fontSize: 24,
    fontWeight: "800",
    color: "#ffffff",
    marginBottom: 4,
  },
  metricSub: {
    fontSize: 11,
    color: "#837996",
  },
  creditsCard: {
    backgroundColor: "#160c24",
    borderRadius: 16,
    padding: 16,
    borderWidth: 1,
    borderColor: "#2e1c4b",
    marginBottom: 18,
  },
  creditsHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
  },
  creditsTitle: {
    color: "#ffffff",
    fontSize: 14,
    fontWeight: "600",
  },
  creditsValue: {
    color: "#bc5cff",
    fontSize: 13,
    fontWeight: "700",
  },
  progressBarBackground: {
    height: 7,
    backgroundColor: "#221338",
    borderRadius: 4,
    overflow: "hidden",
    marginBottom: 8,
  },
  progressBarFill: {
    height: "100%",
    backgroundColor: "#bc5cff",
    borderRadius: 4,
  },
  creditsFooter: {
    fontSize: 11,
    color: "#837996",
  },
  filterSection: {
    marginBottom: 16,
  },
  searchBar: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#160c24",
    borderRadius: 12,
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderWidth: 1,
    borderColor: "#2e1c4b",
    marginBottom: 12,
    gap: 8,
  },
  searchInput: {
    flex: 1,
    color: "#ffffff",
    fontSize: 13,
    padding: 0,
  },
  categoryScroll: {
    flexDirection: "row",
  },
  categoryChip: {
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 18,
    backgroundColor: "#160c24",
    borderWidth: 1,
    borderColor: "#2e1c4b",
    marginRight: 8,
  },
  categoryChipActive: {
    backgroundColor: "#bc5cff",
    borderColor: "#bc5cff",
  },
  categoryChipText: {
    color: "#aaa2b8",
    fontSize: 12,
    fontWeight: "600",
  },
  categoryChipTextActive: {
    color: "#170c22",
    fontWeight: "700",
  },
  recordsListHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
    marginTop: 4,
  },
  sectionTitle: {
    fontSize: 15,
    fontWeight: "700",
    color: "#ffffff",
  },
  recordCount: {
    fontSize: 12,
    color: "#837996",
  },
  loadingContainer: {
    padding: 36,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
  },
  loadingText: {
    color: "#aaa2b8",
    fontSize: 13,
  },
  emptyContainer: {
    padding: 36,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#160c24",
    borderRadius: 16,
    borderWidth: 1,
    borderColor: "#2e1c4b",
  },
  emptyTitle: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "700",
    marginTop: 12,
  },
  emptySubtitle: {
    color: "#837996",
    fontSize: 13,
    textAlign: "center",
    marginTop: 6,
    lineHeight: 18,
  },
  recordCard: {
    backgroundColor: "#160c24",
    borderRadius: 16,
    padding: 14,
    borderWidth: 1,
    borderColor: "#2e1c4b",
    marginBottom: 12,
  },
  recordMain: {
    marginBottom: 10,
  },
  recordInfo: {
    gap: 6,
  },
  tagRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
  },
  statusCompleted: {
    backgroundColor: "rgba(16, 185, 129, 0.15)",
  },
  statusProcessing: {
    backgroundColor: "rgba(245, 158, 11, 0.15)",
  },
  statusFailed: {
    backgroundColor: "rgba(239, 68, 68, 0.15)",
  },
  statusText: {
    fontSize: 10,
    fontWeight: "700",
    color: "#10b981",
  },
  categoryTag: {
    fontSize: 11,
    color: "#bc5cff",
    fontWeight: "600",
    backgroundColor: "#221138",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
  },
  sizeTag: {
    fontSize: 11,
    color: "#837996",
  },
  recordTitle: {
    fontSize: 15,
    fontWeight: "700",
    color: "#ffffff",
  },
  metaRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
  },
  recordMeta: {
    fontSize: 12,
    color: "#aaa2b8",
  },
  dot: {
    color: "#605474",
    marginHorizontal: 4,
  },
  transcriptPreview: {
    fontSize: 12,
    color: "#837996",
    fontStyle: "italic",
    marginTop: 2,
  },
  recordActions: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderTopWidth: 1,
    borderTopColor: "#221338",
    paddingTop: 10,
  },
  playButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    backgroundColor: "#25153d",
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#3c2060",
  },
  playButtonActive: {
    backgroundColor: "#bc5cff",
    borderColor: "#bc5cff",
  },
  playButtonText: {
    color: "#bc5cff",
    fontSize: 12,
    fontWeight: "600",
  },
  playButtonTextActive: {
    color: "#170c22",
    fontWeight: "700",
  },
  deleteButton: {
    padding: 6,
  },
  modalBackdrop: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.75)",
    justifyContent: "flex-end",
  },
  modalCard: {
    backgroundColor: "#160c24",
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    padding: 20,
    borderWidth: 1,
    borderColor: "#371c5a",
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "700",
    color: "#ffffff",
  },
  inputLabel: {
    fontSize: 12,
    fontWeight: "600",
    color: "#aaa2b8",
    marginBottom: 6,
    marginTop: 10,
  },
  modalInput: {
    backgroundColor: "#0b0b12",
    borderWidth: 1,
    borderColor: "#2e1c4b",
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    color: "#ffffff",
    fontSize: 14,
  },
  modalPills: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  modalPill: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
    backgroundColor: "#0b0b12",
    borderWidth: 1,
    borderColor: "#2e1c4b",
  },
  modalPillActive: {
    backgroundColor: "#bc5cff",
    borderColor: "#bc5cff",
  },
  modalPillText: {
    color: "#aaa2b8",
    fontSize: 12,
    fontWeight: "500",
  },
  modalPillTextActive: {
    color: "#170c22",
    fontWeight: "700",
  },
  modalFooter: {
    flexDirection: "row",
    justifyContent: "flex-end",
    gap: 12,
    marginTop: 20,
  },
  cancelButton: {
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 10,
  },
  cancelButtonText: {
    color: "#aaa2b8",
    fontSize: 14,
    fontWeight: "600",
  },
  confirmButton: {
    backgroundColor: "#bc5cff",
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 10,
  },
  confirmButtonText: {
    color: "#170c22",
    fontSize: 14,
    fontWeight: "700",
  },
});
